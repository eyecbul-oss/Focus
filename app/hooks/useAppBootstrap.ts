import { useEffect } from 'react';
import { resetDailyStatsIfNeeded } from '../services/localPersistence';
import { bootstrapSync, setupSync, teardownSync } from '../services/syncService';

export default function useAppBootstrap() {
  useEffect(() => {
    let unsubscribe: (() => void) | null = null;

    async function boot() {
      try {
        await resetDailyStatsIfNeeded();
        await bootstrapSync();
        unsubscribe = setupSync();
      } catch (e) {
        console.log('Bootstrap beklemede:', e);
      }
    }

    boot();

    return () => {
      if (unsubscribe) teardownSync(unsubscribe);
    };
  }, []);
}
