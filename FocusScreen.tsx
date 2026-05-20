import { FocusHistoryItem, FocusTask } from '../store/focusStore';

export type Badge = {
  id: string;
  title: string;
  desc: string;
  unlocked: boolean;
  icon: string;
};

export function calculateBadges(tasks: FocusTask[], history: FocusHistoryItem[], totalToday: number): Badge[] {
  const done = tasks.filter((t) => t.done).length;
  return [
    { id: 'first-task', title: 'İlk Görev', desc: 'Bir görevi tamamla', unlocked: done >= 1, icon: '✅' },
    { id: 'three-tasks', title: '3 Görev', desc: 'Aynı gün 3 görev tamamla', unlocked: done >= 3, icon: '🎯' },
    { id: 'first-focus', title: 'İlk Focus', desc: 'İlk odak seansını bitir', unlocked: history.length >= 1, icon: '🎬' },
    { id: 'hundred-min', title: '100 Dakika', desc: 'Bugün 100 dk çalış', unlocked: totalToday >= 6000, icon: '⏱️' },
    { id: 'ten-sessions', title: '10 Seans', desc: '10 focus seansı tamamla', unlocked: history.length >= 10, icon: '🔥' },
  ];
}

export function getUnlockedBadges(badges: Badge[]) {
  return badges.filter((b) => b.unlocked);
}

export function getLockedBadges(badges: Badge[]) {
  return badges.filter((b) => !b.unlocked);
}
