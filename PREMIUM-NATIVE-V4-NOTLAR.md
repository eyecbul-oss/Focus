import React from 'react';
import { ScrollView, StyleSheet, Text } from 'react-native';
import GradientBackground from '../../components/ui/GradientBackground';
import GlassCard from '../../components/ui/GlassCard';
import NeonButton from '../../components/ui/NeonButton';
import { COLORS } from '../../theme/colors';
import { useAuthStore } from '../../store/authStore';
import { requestNotifications } from '../../services/notificationService';
import { useSettingsStore } from '../../store/settingsStore';
import { saveCloudData, loadCloudData } from '../../services/cloudSyncService';
import { useFocusStore } from '../../store/focusStore';

export default function ProfileScreen() {
  const email = useAuthStore((s) => s.email);
  const guest = useAuthStore((s) => s.guest);
  const cloudStatus = useAuthStore((s) => s.cloudStatus);
  const logout = useAuthStore((s) => s.logout);
  const setNotifications = useSettingsStore((s) => s.setNotifications);
  const setCloudStatus = useAuthStore((s) => s.setCloudStatus);
  const snapshot = useFocusStore((s) => s.snapshot);
  const hydrate = useFocusStore((s) => s.hydrate);

  const testCloud = async () => {
    if (!email || guest) { setCloudStatus('Mail ile giriş yapınca bulut aktif olur.'); return; }
    const uid = email.replace(/[^a-zA-Z0-9]/g, '_');
    try {
      await saveCloudData(uid, { ...snapshot(), updatedAt: new Date().toISOString() });
      const cloud = await loadCloudData(uid);
      if (cloud) hydrate(cloud);
      setCloudStatus('Bulut kontrolü başarılı');
    } catch (e) {
      setCloudStatus('Bulut kontrolü başarısız');
    }
  };

  return (
    <GradientBackground>
      <ScrollView contentContainerStyle={styles.container}>
        <Text style={styles.title}>Profil</Text>
        <GlassCard style={styles.card}>
          <Text style={styles.section}>Hesap</Text>
          <Text style={styles.text}>{guest ? 'Misafir mod' : email}</Text>
          <Text style={styles.muted}>{cloudStatus}</Text>
        </GlassCard>

        <GlassCard style={styles.card}>
          <Text style={styles.section}>Bildirim</Text>
          <Text style={styles.text}>Mola ve seans hatırlatıcılarını açabilirsin.</Text>
          <NeonButton title="Bildirim İznini Aç" onPress={async () => setNotifications(await requestNotifications())} />
          <NeonButton title="Bulutu Kontrol Et" variant="dark" onPress={testCloud} />
        </GlassCard>

        <NeonButton title="Çıkış Yap" variant="danger" onPress={logout} />
      </ScrollView>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { padding: 18, paddingBottom: 110 },
  title: { color: COLORS.text, fontSize: 32, fontWeight: '900', marginTop: 18, marginBottom: 14 },
  card: { marginBottom: 14 },
  section: { color: COLORS.primary, fontWeight: '900', fontSize: 18, marginBottom: 8 },
  text: { color: COLORS.text, fontWeight: '800', lineHeight: 22 },
  muted: { color: COLORS.muted, fontWeight: '800', marginTop: 8 },
});
