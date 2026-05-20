import AsyncStorage from '@react-native-async-storage/async-storage';
import { FocusTask, FocusNote } from '../store/focusStore';

const KEY = 'sezr_focus_state_v5';

export type PersistedFocusState = {
  tasks: FocusTask[];
  notes: FocusNote[];
  focusSeconds: number;
  dailyTarget: number;
  totalToday: number;
  pomodoros: number;
  breakSeconds: number;
  updatedAt: string;
};

export async function saveFocusState(data: PersistedFocusState) {
  await AsyncStorage.setItem(KEY, JSON.stringify({ ...data, updatedAt: new Date().toISOString() }));
}

export async function loadFocusState(): Promise<PersistedFocusState | null> {
  const raw = await AsyncStorage.getItem(KEY);
  return raw ? JSON.parse(raw) : null;
}
