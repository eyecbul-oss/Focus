import * as Notifications from 'expo-notifications';

export async function requestNotifications() {
  const result = await Notifications.requestPermissionsAsync();
  return result.status === 'granted';
}

export async function notifyFocusDone() {
  await Notifications.scheduleNotificationAsync({
    content: {
      title: 'SezR Focus',
      body: 'Odak seansı tamamlandı. Kısa mola iyi gelir.',
    },
    trigger: null,
  });
}
