package ui;

import domain.Right;
import domain.RightType;
import lombok.Data;
import lombok.NonNull;
import services.SessionStatefulService;

import java.util.function.Function;

@Data
public class Command {
    @NonNull String[] path;
    @NonNull String[] args;
    @NonNull String description;
    @NonNull Function<String[], CommandStatus> action;
    RightType[] necessaryRights;

    public Command(@NonNull String path, @NonNull String args,
                   @NonNull String description, @NonNull Function<String[], CommandStatus> action,
                   RightType[] necessaryRights) {
        this.path = parse(path);
        this.args = args.length() == 0 ? new String[]{} : args.split(" ");
        this.description = description;
        this.action = action;
        this.necessaryRights = necessaryRights;
    }

    public boolean isInPath(String[] targetPath) {
        if (targetPath.length > path.length) {
            return false;
        }
        return hasCommonPrefix(targetPath);
    }

    public boolean isSamePath(String[] targetPath) {
        if (targetPath.length != path.length) {
            return false;
        }
        return hasCommonPrefix(targetPath);
    }

    private boolean hasCommonPrefix(String[] targetPath) {
        for (int i = 0; i < Math.min(targetPath.length, path.length); ++i) {
            if (!targetPath[i].equals(path[i])) return false;
        }
        return true;
    }

    private String[] parse(String path) {
        return path.split("/");
    }
}
