import { create } from 'zustand';

type AuthState = {
  uid: string | null;
  email: string | null;
  guest: boolean;
  cloudStatus: string;
  login: (email: string, uid?: string) => void;
  logout: () => void;
  continueGuest: () => void;
  setCloudStatus: (status: string) => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  uid: null,
  email: null,
  guest: false,
  cloudStatus: 'Giriş bekleniyor',
  login: (email, uid) => set({ email, uid: uid || email.replace(/[^a-zA-Z0-9]/g, '_'), guest: false, cloudStatus: 'Bulut senkron aktif' }),
  logout: () => set({ uid: null, email: null, guest: false, cloudStatus: 'Çıkış yapıldı' }),
  continueGuest: () => set({ uid: null, email: null, guest: true, cloudStatus: 'Misafir mod' }),
  setCloudStatus: (cloudStatus) => set({ cloudStatus }),
}));
