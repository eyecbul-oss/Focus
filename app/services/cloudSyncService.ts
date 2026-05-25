import { doc, getDoc, serverTimestamp, setDoc } from 'firebase/firestore';
import { db } from './firebase';

export async function loadCloudData(uid: string) {
  const ref = doc(db, 'focusData', uid);
  const snap = await getDoc(ref);

  if (!snap.exists()) return null;
  return snap.data()?.state ?? null;
}

export async function saveCloudData(uid: string, data: any) {
  const ref = doc(db, 'focusData', uid);

  await setDoc(
    ref,
    {
      state: data,
      updatedAt: serverTimestamp(),
    },
    { merge: true },
  );

  return true;
}

export function mergeLatest(local: any, cloud: any) {
  if (!cloud) return local;
  if (!local) return cloud;

  return {
    ...local,
    ...cloud,
    totalMinutes: Math.max(local.totalMinutes ?? 0, cloud.totalMinutes ?? 0),
    sessions: Math.max(local.sessions ?? 0, cloud.sessions ?? 0),
    streak: Math.max(local.streak ?? 0, cloud.streak ?? 0),
    tasks: cloud.tasks?.length ? cloud.tasks : local.tasks,
    exams: cloud.exams?.length ? cloud.exams : local.exams,
  };
}
