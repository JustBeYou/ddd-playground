package persistence.base.queries;

public enum QueryOperation {
    // Filter operations
    // For numbers
    Equal,
    NotEqual,
    LessThan,
    LessEqualThan,
    GreaterThan,
    GreaterEqualThan,
    // For strings
    Like,
    NotLike,
    // For bools
    IsTrue,
    IsFalse,

    // For any type
    IsSame,
}
