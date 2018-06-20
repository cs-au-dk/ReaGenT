declare class Starfield {
    constructor(config: {
        canvas: string | HTMLCanvasElement,
        vx?: number,
        vy?: number,
        maxRadius?: number,
        maxStars?: number,
        shootingStarInterval?: number
    }): Starfield;
    start(void): void;
}