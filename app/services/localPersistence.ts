import AsyncStorage from '@react-native-async-storage/async-storage';

const KEY = 'focus_state_v1';

export async function saveFocusState(data: any) {
  await AsyncStorage.setItem(KEY, JSON.stringify(data));
}

export async function loadFocusState() {
  const raw = await AsyncStorage.getItem(KEY);
  return raw ? JSON.parse(raw) : null;
}

export async function resetDailyStatsIfNeeded() {
  return true;
}
