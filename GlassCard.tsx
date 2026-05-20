import { FocusTask, FocusHistoryItem } from '../store/focusStore';

export type Badge = {
  id: string;
  title: string;
  desc: string;
  unlocked: boolean;
};

export function calculateBadges(tasks: FocusTask[], history: FocusHistoryItem[], totalToday: number): Badge[] {
  const done = tasks.filter((t) => t.done).length;
  return [
    { id: 'first-task', title: 'İlk Görev', desc: 'Bir görevi tamamla', unlocked: done >= 1 },
    { id: 'three-tasks', title: '3 Görev', desc: 'Aynı gün 3 görev tamamla', unlocked: done >= 3 },
    { id: 'first-focus', title: 'İlk Focus', desc: 'İlk odak seansını bitir', unlocked: history.length >= 1 },
    { id: 'hundred-min', title: '100 Dakika', desc: 'Bugün 100 dk çalış', unlocked: totalToday >= 6000 },
  ];
}
