import {
    useEffect,
    useRef,
    type PointerEvent as ReactPointerEvent,
} from "react";

interface ResizeHandleProps {
    ariaLabel: string;
    value: number;
    min: number;
    max: number;
    onResize: (deltaX: number) => void;
}

export function ResizeHandle({
                                 ariaLabel,
                                 value,
                                 min,
                                 max,
                                 onResize,
                             }: ResizeHandleProps) {
    const cleanupRef = useRef<() => void>(() => undefined);

    useEffect(() => {
        return () => {
            cleanupRef.current();
        };
    }, []);

    function startResize(
        event: ReactPointerEvent<HTMLDivElement>,
    ) {
        event.preventDefault();

        let previousX = event.clientX;

        const previousCursor = document.body.style.cursor;
        const previousUserSelect =
            document.body.style.userSelect;

        document.body.style.cursor = "col-resize";
        document.body.style.userSelect = "none";

        function finishResize() {
            window.removeEventListener(
                "pointermove",
                handlePointerMove,
            );
            window.removeEventListener(
                "pointerup",
                finishResize,
            );
            window.removeEventListener(
                "pointercancel",
                finishResize,
            );

            document.body.style.cursor = previousCursor;
            document.body.style.userSelect =
                previousUserSelect;

            cleanupRef.current = () => undefined;
        }

        function handlePointerMove(
            pointerEvent: PointerEvent,
        ) {
            const deltaX = pointerEvent.clientX - previousX;

            previousX = pointerEvent.clientX;
            onResize(deltaX);
        }

        cleanupRef.current();
        cleanupRef.current = finishResize;

        window.addEventListener(
            "pointermove",
            handlePointerMove,
        );
        window.addEventListener("pointerup", finishResize);
        window.addEventListener(
            "pointercancel",
            finishResize,
        );
    }

    return (
        <div
            role="separator"
            tabIndex={0}
            aria-label={ariaLabel}
            aria-orientation="vertical"
            aria-valuemin={min}
            aria-valuemax={max}
            aria-valuenow={value}
            onPointerDown={startResize}
            onKeyDown={(event) => {
                if (event.key === "ArrowLeft") {
                    event.preventDefault();
                    onResize(-16);
                }

                if (event.key === "ArrowRight") {
                    event.preventDefault();
                    onResize(16);
                }
            }}
            className="group relative cursor-col-resize touch-none outline-none"
        >
            <span className="absolute inset-y-0 left-1/2 w-px -translate-x-1/2 bg-transparent transition-colors group-hover:bg-primary group-focus-visible:bg-primary" />

            <span className="absolute inset-y-0 -left-1 -right-1" />
        </div>
    );
}
