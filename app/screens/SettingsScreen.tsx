import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import GlassCard from '../components/GlassCard';
import { COLORS } from '../theme/colors';
import NeonButton from '../components/NeonButton';
import { scheduleDailyReminder } from '../services/notificationService';

export default function SettingsScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.header}>Ayarlar</Text>

      <GlassCard>
        <Text style={styles.label}>Bildirimler</Text>

        <NeonButton
          title='Gunluk Hatirlatici Ac'
          onPress={() => scheduleDailyReminder()}
        />
      </GlassCard>

      <GlassCard>
        <Text style={styles.small}>Firebase ENV anahtarlari tanimlandiginda cloud sync otomatik aktif olur.</Text>
      </GlassCard>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.bg,
    padding: 20,
  },
  header: {
    color: COLORS.text,
    fontSize: 28,
    fontWeight: '700',
    marginBottom: 20,
  },
  label: {
    color: COLORS.text,
    fontSize: 18,
    marginBottom: 12,
  },
  small: {
    color: COLORS.muted,
    lineHeight: 24,
  },
});
