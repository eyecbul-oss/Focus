import { create } from 'zustand';

type AuthState = {
  email: string | null;
  guest: boolean;
  cloudStatus: string;
  login: (email: string) => void;
  logout: () => void;
  continueGuest: () => void;
  setCloudStatus: (status: string) => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  email: null,
  guest: true,
  cloudStatus: 'Misafir mod',
  login: (email) => set({ email, guest: false, cloudStatus: 'Bulut senkron aktif' }),
  logout: () => set({ email: null, guest: true, cloudStatus: 'Çıkış yapıldı' }),
  continueGuest: () => set({ email: null, guest: true, cloudStatus: 'Misafir mod' }),
  setCloudStatus: (cloudStatus) => set({ cloudStatus }),
}));
