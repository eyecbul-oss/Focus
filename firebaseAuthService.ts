import { useEffect } from 'react';
import { resetDailyStatsIfNeeded } from '../services/localPersistence';
import { bootstrapSync, setupSync, teardownSync } from '../services/syncService';

export default function useAppBootstrap() {
  useEffect(() => {
    let unsubscribe: (() => void) | null = null;

    async function boot() {
      await resetDailyStatsIfNeeded();
      await bootstrapSync();
      unsubscribe = setupSync();
    }

    boot();

    return () => {
      if (unsubscribe) teardownSync(unsubscribe);
    };
  }, []);
}
