import { useAuthStore } from '../store/authStore';
import { useFocusStore } from '../store/focusStore';
import { loadFocusState, saveFocusState } from './localPersistence';
import { loadCloudData, mergeLatest, saveCloudData } from './cloudSyncService';

let syncTimer: ReturnType<typeof setTimeout> | null = null;

export async function performSync() {
  const auth = useAuthStore.getState();
  const focus = useFocusStore.getState();

  await saveFocusState(focus.snapshot());

  if (!auth.email || auth.guest) {
    auth.setCloudStatus('Misafir mod: kayıtlar bu cihazda');
    return;
  }

  try {
    const uid = auth.uid || auth.email.replace(/[^a-zA-Z0-9]/g, '_');
    const local = focus.snapshot();
    const cloud = await loadCloudData(uid);
    const merged = mergeLatest(local, cloud);
    useFocusStore.getState().hydrate(merged);
    await saveCloudData(uid, merged);
    auth.setCloudStatus('Bulut senkron aktif');
  } catch (e) {
    auth.setCloudStatus('Bulut senkron beklemede');
  }
}

export function debouncedSync(delay = 2500) {
  if (syncTimer) clearTimeout(syncTimer);
  syncTimer = setTimeout(() => performSync(), delay);
}

export function setupSync() {
  return useFocusStore.subscribe(() => debouncedSync());
}

export function teardownSync(unsubscribe: () => void) {
  if (syncTimer) clearTimeout(syncTimer);
  unsubscribe();
}

export async function bootstrapSync() {
  const local = await loadFocusState();
  if (local) useFocusStore.getState().hydrate(local);
  await performSync();
}
