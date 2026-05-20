import { doc, getDoc, setDoc } from 'firebase/firestore';
import { db } from './firebase';
import { FocusSnapshot } from '../store/focusStore';

export async function saveCloudData(uid: string, data: FocusSnapshot) {
  await setDoc(doc(db, 'focusUsers', uid), { ...data, updatedAt: new Date().toISOString() }, { merge: true });
}

export async function loadCloudData(uid: string): Promise<FocusSnapshot | null> {
  const snap = await getDoc(doc(db, 'focusUsers', uid));
  return snap.exists() ? (snap.data() as FocusSnapshot) : null;
}

export function mergeLatest(local: FocusSnapshot, cloud: FocusSnapshot | null): FocusSnapshot {
  if (!cloud) return local;
  const localTime = new Date(local.updatedAt || 0).getTime();
  const cloudTime = new Date(cloud.updatedAt || 0).getTime();
  return cloudTime > localTime ? cloud : local;
}
