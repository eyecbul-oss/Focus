import { useEffect } from 'react';
import { useFocusStore } from '../store/focusStore';
import { loadFocusState, saveFocusState } from '../services/localPersistence';
import { useAuthStore } from '../store/authStore';
import { loadCloudData, saveCloudData } from '../services/cloudSyncService';

export default function useAppBootstrap() {
  const hydrate = useFocusStore((s) => s.hydrate);
  const snapshot = useFocusStore((s) => s.snapshot);
  const email = useAuthStore((s) => s.email);
  const guest = useAuthStore((s) => s.guest);
  const setCloudStatus = useAuthStore((s) => s.setCloudStatus);

  useEffect(() => {
    loadFocusState().then((local) => {
      if (local) hydrate(local);
    });
  }, [hydrate]);

  useEffect(() => {
    const unsubscribe = useFocusStore.subscribe((state) => {
      saveFocusState({ ...state.snapshot(), updatedAt: new Date().toISOString() });
    });
    return unsubscribe;
  }, []);

  useEffect(() => {
    async function sync() {
      if (!email || guest) return;

      try {
        const uid = email.replace(/[^a-zA-Z0-9]/g, '_');
        const cloud = await loadCloudData(uid);
        if (cloud) hydrate(cloud);
        await saveCloudData(uid, { ...snapshot(), updatedAt: new Date().toISOString() });
        setCloudStatus('Bulut senkron aktif');
      } catch (e) {
        setCloudStatus('Bulut senkron beklemede');
      }
    }

    sync();
  }, [email, guest]);
}
