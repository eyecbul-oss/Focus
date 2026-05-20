import * as Notifications from 'expo-notifications';
import AsyncStorage from '@react-native-async-storage/async-storage';

export async function requestNotifications() {
  const result = await Notifications.requestPermissionsAsync();
  return result.status === 'granted';
}

export async function sendNotification(title: string, body: string, key = 'general', cooldownMs = 60000) {
  const now = Date.now();
  const storageKey = `notif_${key}`;
  const lastRaw = await AsyncStorage.getItem(storageKey);
  const last = lastRaw ? Number(lastRaw) : 0;

  if (now - last < cooldownMs) return false;

  await Notifications.scheduleNotificationAsync({
    content: { title, body, sound: 'default' },
    trigger: null,
  });

  await AsyncStorage.setItem(storageKey, String(now));
  return true;
}

export async function notifyFocusComplete(minutes: number) {
  return sendNotification('Focus Seansı Tamamlandı', `${minutes} dakika başarıyla çalıştın.`, 'focus-complete');
}

export async function notifyBreakComplete() {
  return sendNotification('Mola Bitti', 'Çalışmaya dönmeye hazırsın.', 'break-complete');
}

export async function notifyDailyGoal(completed: number, target: number) {
  const remaining = target - completed;
  if (remaining <= 0) return sendNotification('Günlük Hedef Tamamlandı', `${target} dakikayı tamamladın.`, 'daily-goal');
  return sendNotification('Günlük Hedef Hatırlatması', `${remaining} dakika daha çalışman gerekiyor.`, 'daily-reminder');
}
