export interface IDictionary<X> {
    [index: string]: X
}

export interface ResultSummary {
    violations: IDictionary<TypeViolation[]>
}

export interface TypeViolation {
    message: string
    path: string
}

export interface ResultIndex {
    index: IDictionary<String>
}
