// 🏆 Başarı Rozeti Servisi - Focus Uygulaması
import { FocusTask, FocusHistoryItem } from '../store/focusStore';

export type Badge = {
  id: string;
  title: string;
  desc: string;
  icon: string;
  unlocked: boolean;
  progress?: number; // 0-100
};

/**
 * Rozet hesapla
 */
export function calculateBadges(
  tasks: FocusTask[],
  history: FocusHistoryItem[],
  totalToday: number
): Badge[] {
  const done = tasks.filter((t) => t.done).length;
  const totalMinutes = history.reduce((sum, h) => sum + h.minutes, 0);
  const todayMinutes = totalToday / 60;

  return [
    {
      id: 'first-task',
      title: 'İlk Adım',
      desc: 'Bir görevi tamamla',
      icon: '🎯',
      unlocked: done >= 1,
      progress: Math.min((done / 1) * 100, 100),
    },
    {
      id: 'three-tasks',
      title: '3 Görev',
      desc: 'Aynı gün 3 görev tamamla',
      icon: '⚡',
      unlocked: done >= 3,
      progress: Math.min((done / 3) * 100, 100),
    },
    {
      id: 'ten-tasks',
      title: '10 Görev',
      desc: 'Aynı gün 10 görev tamamla',
      icon: '🔥',
      unlocked: done >= 10,
      progress: Math.min((done / 10) * 100, 100),
    },
    {
      id: 'first-focus',
      title: 'İlk Focus',
      desc: 'İlk odak seansını bitir',
      icon: '🎬',
      unlocked: history.length >= 1,
      progress: history.length >= 1 ? 100 : 0,
    },
    {
      id: 'hundred-min',
      title: '100 Dakika',
      desc: 'Bugün 100 dk çalış',
      icon: '⏱️',
      unlocked: todayMinutes >= 100,
      progress: Math.min((todayMinutes / 100) * 100, 100),
    },
    {
      id: 'thousand-min',
      title: 'Bin Dakika',
      desc: 'Toplam 1000 dk çalış',
      icon: '🏆',
      unlocked: totalMinutes >= 1000,
      progress: Math.min((totalMinutes / 1000) * 100, 100),
    },
    {
      id: 'perfect-day',
      title: 'Mükemmel Gün',
      desc: 'Günlük hedefini tamamla',
      icon: '⭐',
      unlocked: done >= 5 && todayMinutes >= 60,
      progress: (Math.min((done / 5) * 100, 100) + Math.min((todayMinutes / 60) * 100, 100)) / 2,
    },
  ];
}

/**
 * Sadece kilidi açılan rozetleri al
 */
export function getUnlockedBadges(badges: Badge[]): Badge[] {
  return badges.filter((b) => b.unlocked);
}

/**
 * Sadece kilitli rozetleri al
 */
export function getLockedBadges(badges: Badge[]): Badge[] {
  return badges.filter((b) => !b.unlocked);
}
