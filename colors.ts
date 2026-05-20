import { doc, getDoc, setDoc } from 'firebase/firestore';
import { db } from './firebase';
import { FocusTask, FocusNote, FocusHistoryItem, ExamSettings } from '../store/focusStore';

export type CloudFocusData = {
  tasks: FocusTask[];
  notes: FocusNote[];
  history: FocusHistoryItem[];
  exam: ExamSettings;
  totalToday: number;
  pomodoros: number;
  updatedAt: string;
};

export async function saveCloudData(uid: string, data: CloudFocusData) {
  await setDoc(doc(db, 'focusUsers', uid), data, { merge: true });
}

export async function loadCloudData(uid: string): Promise<CloudFocusData | null> {
  const snap = await getDoc(doc(db, 'focusUsers', uid));
  return snap.exists() ? (snap.data() as CloudFocusData) : null;
}
