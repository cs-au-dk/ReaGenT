declare interface npm$joiBrowser$Schema {}

interface AnySchema<T: npm$joiBrowser$Schema> {
    forbidden(): T
}

declare interface BooleanSchema extends AnySchema<BooleanSchema> {
    falsy(value: mixed): BooleanSchema
}

declare module "joi-browser" {
    declare module.exports: {
        bool(): BooleanSchema,
    };
}
