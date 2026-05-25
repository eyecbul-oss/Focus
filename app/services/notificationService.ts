import * as Notifications from 'expo-notifications';

Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: true,
    shouldSetBadge: false,
  }),
});

export async function requestNotificationPermission() {
  const current = await Notifications.getPermissionsAsync();
  if (current.status === 'granted') return true;
  const requested = await Notifications.requestPermissionsAsync();
  return requested.status === 'granted';
}

export async function scheduleFocusFinishedNotification(minutes: number) {
  const ok = await requestNotificationPermission();
  if (!ok) return null;

  return Notifications.scheduleNotificationAsync({
    content: {
      title: 'Focus tamamlandı',
      body: `${minutes} dakikalık çalışma seansın bitti.`,
      sound: true,
    },
    trigger: { seconds: Math.max(1, minutes * 60) },
  });
}

export async function scheduleDailyReminder(hour = 20, minute = 0) {
  const ok = await requestNotificationPermission();
  if (!ok) return null;

  return Notifications.scheduleNotificationAsync({
    content: {
      title: 'Bugünkü odak zamanı',
      body: 'Kısa bir seansla ritmini koru.',
      sound: true,
    },
    trigger: {
      hour,
      minute,
      repeats: true,
    },
  });
}
