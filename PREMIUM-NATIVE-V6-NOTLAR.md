import React from 'react';
import { Modal, StyleSheet, Text, View } from 'react-native';
import NeonButton from '../../components/ui/NeonButton';
import { COLORS } from '../../theme/colors';

function fmt(sec: number) {
  const m = Math.floor(sec / 60);
  const s = sec % 60;
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
}

export default function FullscreenFocusView({
  visible,
  remaining,
  running,
  onToggle,
  onExit,
}: {
  visible: boolean;
  remaining: number;
  running: boolean;
  onToggle: () => void;
  onExit: () => void;
}) {
  return (
    <Modal visible={visible} animationType="fade">
      <View style={styles.root}>
        <Text style={styles.label}>SezR Focus</Text>
        <Text style={styles.time}>{fmt(remaining)}</Text>
        <Text style={styles.info}>{running ? 'Odak modundasın.' : 'Kaldığın yerden devam edebilirsin.'}</Text>
        <View style={styles.actions}>
          <NeonButton title={running ? 'Duraklat' : 'Başlat'} onPress={onToggle} />
          <NeonButton title="Çık" variant="dark" onPress={onExit} />
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: '#020617',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 22,
  },
  label: { color: COLORS.primary, fontWeight: '900', fontSize: 20, marginBottom: 20 },
  time: { color: COLORS.text, fontWeight: '900', fontSize: 104, letterSpacing: -6 },
  info: { color: COLORS.muted, fontWeight: '900', marginTop: 10, marginBottom: 30 },
  actions: { width: '100%', gap: 12 },
});
