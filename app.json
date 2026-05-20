import React from 'react';
import { Modal, StyleSheet, Text, View } from 'react-native';
import GlassCard from '../ui/GlassCard';
import NeonButton from '../ui/NeonButton';
import { COLORS } from '../../theme/colors';

function fmt(sec: number) {
  const m = Math.floor(sec / 60);
  const s = sec % 60;
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
}

export default function BreakModal({
  visible,
  remaining,
  running,
  onToggle,
  onFinish,
}: {
  visible: boolean;
  remaining: number;
  running: boolean;
  onToggle: () => void;
  onFinish: () => void;
}) {
  return (
    <Modal visible={visible} animationType="fade" transparent>
      <View style={styles.backdrop}>
        <GlassCard style={styles.box}>
          <Text style={styles.label}>Mola</Text>
          <Text style={styles.time}>{fmt(remaining)}</Text>
          <Text style={styles.info}>Bitirince çalışma süren kaldığı yerden devam eder.</Text>
          <View style={styles.actions}>
            <NeonButton title={running ? 'Duraklat' : 'Başlat'} onPress={onToggle} />
            <NeonButton title="Bitir ve Dön" variant="dark" onPress={onFinish} />
          </View>
        </GlassCard>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  backdrop: { flex: 1, backgroundColor: 'rgba(2,6,23,0.92)', alignItems: 'center', justifyContent: 'center', padding: 18 },
  box: { width: '100%', alignItems: 'center' },
  label: { color: COLORS.primary, fontWeight: '900', fontSize: 18, marginBottom: 14 },
  time: { color: COLORS.text, fontSize: 86, fontWeight: '900', letterSpacing: -4 },
  info: { color: COLORS.muted, fontWeight: '800', textAlign: 'center', marginVertical: 16 },
  actions: { width: '100%', gap: 10 },
});
