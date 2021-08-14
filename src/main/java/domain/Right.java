package domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class Right {
    @NonNull private RightType type;
    @NonNull private String userName;
}
