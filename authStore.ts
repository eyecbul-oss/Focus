import AsyncStorage from '@react-native-async-storage/async-storage';
import { FocusTask } from '../store/focusStore';

const KEY = 'sezr_focus_local_data';

export type LocalFocusData = {
  tasks: FocusTask[];
  totalToday: number;
  updatedAt: string;
};

export async function saveLocalData(data: LocalFocusData) {
  await AsyncStorage.setItem(KEY, JSON.stringify(data));
}

export async function loadLocalData(): Promise<LocalFocusData | null> {
  const raw = await AsyncStorage.getItem(KEY);
  return raw ? JSON.parse(raw) : null;
}
