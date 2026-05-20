import AsyncStorage from '@react-native-async-storage/async-storage';
import { FocusSnapshot } from '../store/focusStore';

const KEY = 'sezr_focus_state_v7';
const DATE_KEY = 'sezr_focus_date';

export async function saveFocusState(data: FocusSnapshot) {
  await AsyncStorage.setItem(KEY, JSON.stringify({ ...data, updatedAt: new Date().toISOString() }));
  await AsyncStorage.setItem(DATE_KEY, new Date().toDateString());
}

export async function loadFocusState(): Promise<FocusSnapshot | null> {
  const raw = await AsyncStorage.getItem(KEY);
  return raw ? JSON.parse(raw) : null;
}

export async function resetDailyStatsIfNeeded() {
  const savedDate = await AsyncStorage.getItem(DATE_KEY);
  const today = new Date().toDateString();
  if (savedDate && savedDate !== today) {
    const data = await loadFocusState();
    if (data) {
      await saveFocusState({ ...data, totalToday: 0, pomodoros: 0 });
    }
  }
}
