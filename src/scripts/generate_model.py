from sys import argv
from pathlib import Path
from re import findall, match

basic_types = ["Integer", "String", "Boolean"]

if len(argv) < 2 or len(argv) > 3:
    print(f"Usage: {argv[0]} <model name> [root dir]")
    exit()

model_name = argv[1]

current_path = Path(__file__).parent.resolve()
root_dir = argv[2] if len(argv) == 3 else (current_path / "../main/java").resolve()

print(f"[i] Will generate model for entity {model_name} from root {root_dir}")

domain_all_path = root_dir / "domain/"
domain_src_path = root_dir / f"domain/{model_name}.java"
model_src_path = root_dir / f"persistence/models/{model_name}Model.java"
model_factory_src_path = root_dir / f"persistence/models/{model_name}ModelFactory.java"

def read_config(name):
    content = open(name).read()
    lines = content.split("\n")
    parts = [line.split('=') for line in lines if line != '']
    conf = {}
    for key, values in parts:
        values = values.split(',')
        conf[key] = values
    return conf

models_config = read_config(current_path / "models.config")
models_config["classes"] = []
models_config["enums"] = []
for domain_file in domain_all_path.iterdir():
    domain_class = domain_file.name.replace(".java", "")
    content = open(domain_file).read()
    is_enum = "public enum" in content

    if is_enum:
        models_config["enums"].append(domain_class)
    else:
        models_config["classes"].append(domain_class)

print()

print(f"[i] Parsing domain class from {domain_src_path}")
domain_content = open(domain_src_path).read()
found_fields = findall(r"private ([A-Z]+[a-zA-Z<>]+) ([a-zA-Z]+[a-zA-Z]+);", domain_content)
fields = []
simple_field_names = []
for found_field in found_fields:
    field = {
        "type": found_field[0],
        "name": found_field[1],
        "is_reference": False,
    }

    display_coll_type = ""
    if "Collection" in field["type"]:
        field["is_reference"] = True
        coll_type_match = match(r"Collection<([a-zA-Z]+)>", field["type"])
        field["type"] = "Collection"
        field["coll_type"] = coll_type_match[1]
        display_coll_type = " " + field["coll_type"]
    else:
        if field["type"] in models_config["classes"]:
            field["is_reference"] = True

    print(f"[+] Found field {field['name']} of type {field['type']}{display_coll_type}.")
    fields.append(field)

print()

print(f"[i] Generating model sources in {model_src_path}")
model_template = open(current_path / "./Model.java.template").read()

for field in fields:
    if "coll_type" not in field and field["type"] not in models_config["classes"]:
        simple_field_names.append(field["name"])

imports = f"import domain.{model_name};\n"
for field in fields:
    if field["type"] not in basic_types:
        t = field["type"] if "coll_type" not in field else field["coll_type"]
        imports += f"import domain.{t};\n"
    if field["is_reference"]:
        t = field["type"] if "coll_type" not in field else field["coll_type"]
        imports += f"import persistence.models.{t}Model;\n"
    print (field)

map_put = ""
map_get = ""
map_get_if_set = ""
get_fields = ""

# map.put("name", new Field("name", FieldType.String, this.data.getName()));
# fields.add(new Field("id", FieldType.Integer));
#        if (!map.getMap().containsKey("name")) {
#            map.getMap().put("name", new Field("name", FieldType.String, exitingData.getName()));
#        }
for field in fields:
    ftype = field["type"]
    if ftype in models_config["enums"]:
        ftype = "String"
    elif ftype in models_config["classes"] or ftype == "Collection":
        ftype = "Reference"

    if field["type"] not in models_config["classes"] and "coll_type" not in field:
        capitalized = field["name"][0].upper() + field["name"][1:]
        getter = f"this.data.get{capitalized}().toString()"
        map_put += " "*8 + f'map.put("{field["name"]}", new Field("{field["name"]}", FieldType.{ftype}, {getter}));\n'

        if field["type"] not in models_config["enums"]:
            map_get += " "*8 + f'var {field["name"]} = map.getMap().get("{field["name"]}").getValue();\n'
        else:
            map_get += " "*8 + f'var {field["name"]} = {field["type"]}.valueOf(map.getMap().get("{field["name"]}").getValue());\n'

        map_get_if_set += " "*8 + f'if (!map.getMap().containsKey("{field["name"]}")) ' +'{ '
        map_get_if_set += f'map.getMap().put("{field["name"]}", new Field("{field["name"]}", FieldType.{ftype}, exitingData.get{capitalized}().toString()));'
        map_get_if_set += ' }\n'

    get_fields += " "*8 + f'fields.add(new Field("{field["name"]}", FieldType.{ftype}));\n'


should_pass_copy = ""
if model_name in models_config["with_copy"]:
    should_pass_copy = ", true"
map_get += " "*8 + f"this.data = new {model_name}({', '.join(simple_field_names)}{should_pass_copy});"

load_relations = ""

#if ("author".equals(field)) {
#            this.getData().setAuthor((Author) object);
#        }

#if (field.equals("books")) {
#            var entities = (Collection<BookModel>) object;
#            var books = this.data.getBooks();
#            if (!books.isEmpty()) {
#                books.clear();
#            }
#            books.addAll(entities.stream().map(entity -> entity.getData()).collect(Collectors.toList()));
#        }

for field in fields:
    capitalized = field["name"][0].upper() + field["name"][1:]

    if field["is_reference"] and field["type"] == "Collection":

        
        load_relations += " "*8 + f'if ("{field["name"]}".equals(field))'+ '{ \n'
        load_relations += " "*12 + f'var entities = (Collection<{field["coll_type"]}Model>) object; \n'
        load_relations += " "*12 + f'var data = this.data.get{capitalized}();\n'
        load_relations += " "*12 + f'if (!data.isEmpty())'+' {\n'
        load_relations += " "*16 + f'data.clear();\n'
        load_relations += " "*12 + '} \n'
        load_relations += " "*12 + f'data.addAll(entities.stream().map(entity -> entity.getData()).collect(Collectors.toList()));\n'
        load_relations += " "*8 + ' }\n'
    elif field["is_reference"]:
        load_relations += " "*8 + f'if ("{field["name"]}".equals(field))'+ '{ '
        load_relations += " "*8 + f'this.getData().set{capitalized}(({field["type"]}) object);' + ' }\n'

"""\
return new RelatedField[]{
        new RelatedField(
            RelationType.ONE_OWNS_MANY,
            "Author",
            "name",
            new Field("books", FieldType.Reference),
            "Book",
            "authorName",
            new Field("author", FieldType.Reference)
        )
    };
"""

related_fields = f'        return new RelatedField[]{"{"}'
for relation in models_config['relations']:
    entities, keys, fields, rel_type = relation.split("@")
    entity_from, entity_to = entities.split('->')
    key_from, key_to = keys.split('->')
    field_from, field_to = fields.split('->')

    if model_name not in [entity_from, entity_to]:
        pass

    related_fields += f"""
            new RelatedField(
                RelationType.{rel_type},
                "{entity_from}",
                "{key_from}",
                new Field("{field_from}", FieldType.Reference),
                "{entity_to}",
                "{key_to}",
                new Field("{field_to}", FieldType.Reference)
            ),"""
related_fields += "\n" + " "*8 + f'{"}"};'

model_src = model_template\
    .replace("[IMPORTS]", imports)\
    .replace("[CLASS]", model_name)\
    .replace("[MAP_PUT]", map_put)\
    .replace("[MAP_GET]", map_get)\
    .replace("[GET_FIELDS]", get_fields)\
    .replace("[MAP_GET_IF_SET]", map_get_if_set)\
    .replace("[LOAD_RELATIONS]", load_relations)\
    .replace("[RELATED_FIELDS]", related_fields)

open(model_src_path, "w").write(model_src)

print(f"[i] Generating model factory sources in {model_factory_src_path}")

model_factory_template = open(current_path / "./ModelFactory.java.template").read()
model_factory_src = model_factory_template\
    .replace("[CLASS]", model_name)

open(model_factory_src_path, "w").write(model_factory_src)