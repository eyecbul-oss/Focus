import React from 'react';
import { ScrollView, StyleSheet, Text, View } from 'react-native';
import GradientBackground from '../../components/ui/GradientBackground';
import GlassCard from '../../components/ui/GlassCard';
import NeonButton from '../../components/ui/NeonButton';
import StatCard from '../../components/ui/StatCard';
import { COLORS } from '../../theme/colors';
import { useFocusStore } from '../../store/focusStore';

export default function HomeScreen() {
  const tasks = useFocusStore((s) => s.tasks);
  const totalToday = useFocusStore((s) => s.totalToday);
  const pomodoros = useFocusStore((s) => s.pomodoros);
  const exam = useFocusStore((s) => s.exam);
  const done = tasks.filter((t) => t.done).length;
  const rate = tasks.length ? Math.round((done / tasks.length) * 100) : 0;
  const minutes = Math.floor(totalToday / 60);
  const score = Math.min(100, Math.round(minutes * 0.8 + rate * 0.5 + pomodoros * 6));
  const critical = tasks.filter((t) => !t.done && t.priority === 'critical').length;

  return (
    <GradientBackground>
      <ScrollView contentContainerStyle={styles.container} showsVerticalScrollIndicator={false}>
        <View style={styles.header}>
          <View style={styles.logo}><Text style={styles.logoText}>S<Text style={styles.logoGold}>R</Text></Text></View>
          <View style={{ flex: 1 }}>
            <Text style={styles.title}>SezR Focus</Text>
            <Text style={styles.subtitle}>Görev • Odak • Sınav Koçu</Text>
          </View>
          <View style={styles.liveBadge}><Text style={styles.liveText}>Aktif</Text></View>
        </View>

        <GlassCard style={styles.hero}>
          <View style={styles.badgeRow}>
            <Text style={styles.badge}>Öğrenci Koçu</Text>
            <Text style={styles.badgeMuted}>{exam.type}</Text>
          </View>
          <Text style={styles.heroTitle}>Bugünkü çalışma ritmini birlikte başlatalım.</Text>
          <Text style={styles.heroText}>
            {critical > 0 ? `Önce ${critical} kritik görevi bitir. Sonra hafif görevlere geç.` : tasks.length === 0 ? 'İlk görevini ekle, 10 dakikalık kısa seansla başla.' : 'İlk göreve odaklan. Kısa bir seans yeterli.'}
          </Text>
          <View style={styles.heroActions}>
            <NeonButton title="Quick Start" variant="gold" style={styles.actionButton} />
            <NeonButton title="Görev Ekle" variant="dark" style={styles.actionButton} />
          </View>
        </GlassCard>

        <View style={styles.stats}>
          <StatCard value={`${minutes} dk`} label="Bugün" accent="gold" />
          <StatCard value={`%${rate}`} label="Görev" accent="green" />
          <StatCard value={`${score}`} label="Skor" accent="cyan" />
        </View>

        <View style={styles.smartGrid}>
          <GlassCard style={styles.smartCard}>
            <Text style={styles.smartTitle}>Akıllı Öneri</Text>
            <Text style={styles.smartText}>{tasks.length === done && tasks.length > 0 ? 'Bugünkü görevler tamam. Kısa tekrar yeterli.' : 'Kalan görevlerden birini seç ve sayaçla başla.'}</Text>
          </GlassCard>
          <GlassCard style={styles.smartCard}>
            <Text style={styles.smartTitle}>Mini Hedef</Text>
            <Text style={styles.smartText}>{minutes === 0 ? '10 dakika + 1 görev.' : `${Math.max(0, 60 - minutes)} dk daha çalış.`}</Text>
          </GlassCard>
        </View>

        <GlassCard style={styles.review}>
          <Text style={styles.section}>Gün Sonu Kontrolü</Text>
          <Text style={styles.reviewText}>Bugün {minutes} dakika çalıştın, {done}/{tasks.length} görev tamamlandı. Hedefe yakın kalmak için kısa bir seans daha iyi olur.</Text>
        </GlassCard>
      </ScrollView>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { padding: 18, paddingBottom: 110 },
  header: { flexDirection: 'row', alignItems: 'center', gap: 12, marginTop: 18, marginBottom: 18 },
  logo: { width: 58, height: 58, borderRadius: 22, backgroundColor: 'rgba(250,204,21,0.16)', alignItems: 'center', justifyContent: 'center', borderWidth: 1, borderColor: 'rgba(250,204,21,0.32)' },
  logoText: { color: COLORS.text, fontWeight: '900', fontSize: 24 },
  logoGold: { color: COLORS.primary },
  title: { color: COLORS.text, fontSize: 26, fontWeight: '900', letterSpacing: -1 },
  subtitle: { color: COLORS.muted, fontWeight: '800', marginTop: 3 },
  liveBadge: { paddingHorizontal: 12, minHeight: 34, borderRadius: 999, backgroundColor: 'rgba(34,197,94,0.12)', borderWidth: 1, borderColor: 'rgba(34,197,94,0.26)', justifyContent: 'center' },
  liveText: { color: '#BBF7D0', fontWeight: '900', fontSize: 12 },
  hero: { marginBottom: 14, padding: 22 },
  badgeRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 14 },
  badge: { color: COLORS.primary, fontWeight: '900' },
  badgeMuted: { color: COLORS.muted, fontWeight: '900' },
  heroTitle: { color: COLORS.text, fontSize: 34, lineHeight: 38, fontWeight: '900', letterSpacing: -1.5 },
  heroText: { color: COLORS.softText, fontWeight: '800', lineHeight: 23, marginVertical: 16 },
  heroActions: { flexDirection: 'row', gap: 10 },
  actionButton: { flex: 1 },
  stats: { flexDirection: 'row', gap: 10, marginBottom: 14 },
  smartGrid: { flexDirection: 'row', gap: 10, marginBottom: 14 },
  smartCard: { flex: 1, borderRadius: 24 },
  smartTitle: { color: COLORS.primary, fontWeight: '900', marginBottom: 8 },
  smartText: { color: COLORS.softText, fontWeight: '800', lineHeight: 20, fontSize: 13 },
  review: { marginBottom: 18 },
  section: { color: COLORS.primary, fontWeight: '900', fontSize: 18, marginBottom: 8 },
  reviewText: { color: COLORS.text, fontWeight: '800', lineHeight: 22 },
});
