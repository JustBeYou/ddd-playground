package domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class Right {
    @NonNull RightType type;
    @NonNull String userName;
}
