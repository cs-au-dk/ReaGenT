function concat(a: string | number, b: string | number) : string | number {
    return (a as any) + b;
}