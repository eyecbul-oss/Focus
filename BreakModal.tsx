import React, { useMemo, useState } from 'react';
import { ScrollView, StyleSheet, Text, TextInput, View } from 'react-native';
import GradientBackground from '../../components/ui/GradientBackground';
import GlassCard from '../../components/ui/GlassCard';
import NeonButton from '../../components/ui/NeonButton';
import TaskCard from '../../components/tasks/TaskCard';
import TaskFilters, { TaskFilter } from '../../components/tasks/TaskFilters';
import { COLORS } from '../../theme/colors';
import { useFocusStore } from '../../store/focusStore';

export default function TasksScreen() {
  const [title, setTitle] = useState('');
  const [filter, setFilter] = useState<TaskFilter>('all');
  const tasks = useFocusStore((s) => s.tasks);
  const addTask = useFocusStore((s) => s.addTask);
  const toggleTask = useFocusStore((s) => s.toggleTask);
  const deleteTask = useFocusStore((s) => s.deleteTask);
  const clearDoneTasks = useFocusStore((s) => s.clearDoneTasks);

  const visibleTasks = useMemo(() => {
    if (filter === 'active') return tasks.filter((t) => !t.done);
    if (filter === 'completed') return tasks.filter((t) => t.done);
    if (filter === 'critical') return tasks.filter((t) => t.priority === 'critical');
    if (filter === 'normal') return tasks.filter((t) => t.priority === 'normal');
    if (filter === 'light') return tasks.filter((t) => t.priority === 'light');
    return tasks;
  }, [tasks, filter]);

  return (
    <GradientBackground>
      <ScrollView contentContainerStyle={styles.container}>
        <Text style={styles.title}>Görevler</Text>
        <GlassCard style={styles.inputCard}>
          <TextInput placeholder="Örn. Matematik: Problemler 20 soru" placeholderTextColor={COLORS.muted} value={title} onChangeText={setTitle} style={styles.input} />
          <View style={styles.row}>
            <NeonButton title="Normal" variant="dark" onPress={() => { if (title.trim()) { addTask(title.trim(), 'Soru', 'normal'); setTitle(''); } }} style={styles.flex} />
            <NeonButton title="Kritik" onPress={() => { if (title.trim()) { addTask(title.trim(), 'Soru', 'critical'); setTitle(''); } }} style={styles.flex} />
          </View>
        </GlassCard>
        <TaskFilters active={filter} onChange={setFilter} />
        <View style={styles.headRow}>
          <Text style={styles.section}>Bugünkü Görevler</Text>
          <Text style={styles.clear} onPress={clearDoneTasks}>Tamamlananları sil</Text>
        </View>
        {visibleTasks.map((task) => <TaskCard key={task.id} task={task} onPress={() => toggleTask(task.id)} onDelete={() => deleteTask(task.id)} />)}
      </ScrollView>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { padding: 18, paddingBottom: 110 },
  title: { color: COLORS.text, fontSize: 32, fontWeight: '900', marginTop: 18, marginBottom: 14 },
  inputCard: { gap: 12, marginBottom: 18 },
  input: { minHeight: 52, borderRadius: 18, backgroundColor: 'rgba(2,6,23,0.48)', color: COLORS.text, paddingHorizontal: 14, fontWeight: '800', borderWidth: 1, borderColor: COLORS.border },
  row: { flexDirection: 'row', gap: 10 },
  flex: { flex: 1 },
  headRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  section: { color: COLORS.primary, fontSize: 18, fontWeight: '900', marginBottom: 12 },
  clear: { color: COLORS.muted, fontWeight: '900', fontSize: 12 },
});
