import { useEffect, useState } from 'react';

export default function useFocusTimer(initialMinutes = 25) {
  const [secondsLeft, setSecondsLeft] = useState(initialMinutes * 60);
  const [running, setRunning] = useState(false);

  useEffect(() => {
    if (!running) return;

    const timer = setInterval(() => {
      setSecondsLeft((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          setRunning(false);
          return 0;
        }

        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [running]);

  const minutes = Math.floor(secondsLeft / 60)
    .toString()
    .padStart(2, '0');

  const seconds = (secondsLeft % 60).toString().padStart(2, '0');

  return {
    running,
    setRunning,
    reset: () => setSecondsLeft(initialMinutes * 60),
    time: `${minutes}:${seconds}`,
    totalMinutes: initialMinutes,
  };
}
