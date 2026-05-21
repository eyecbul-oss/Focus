import React from 'react';
import { StyleSheet, View } from 'react-native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';
import HomeScreen from '../screens/Home/HomeScreen';
import FocusScreen from '../screens/Focus/FocusScreen';
import TasksScreen from '../screens/Tasks/TasksScreen';
import StatsScreen from '../screens/Stats/StatsScreen';
import ExamScreen from '../screens/Exam/ExamScreen';
import NotesScreen from '../screens/Notes/NotesScreen';
import ProfileScreen from '../screens/Profile/ProfileScreen';
import { COLORS } from '../theme/colors';

const Tab = createBottomTabNavigator();

const icons: Record<string, keyof typeof Ionicons.glyphMap> = {
  Koç: 'sparkles',
  Görev: 'checkbox',
  Sayaç: 'timer',
  Sınav: 'calendar',
  Ritim: 'stats-chart',
  Not: 'create',
  Profil: 'person',
};

export default function BottomTabs() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarStyle: styles.bar,
        tabBarActiveTintColor: COLORS.primary,
        tabBarInactiveTintColor: COLORS.muted,
        tabBarLabelStyle: styles.label,
        tabBarIcon: ({ color, size, focused }) => (
          <View style={[styles.iconWrap, focused && styles.iconActive]}>
            <Ionicons name={icons[route.name]} color={focused ? '#111827' : color} size={size} />
          </View>
        ),
      })}
    >
      <Tab.Screen name="Koç" component={HomeScreen} />
      <Tab.Screen name="Görev" component={TasksScreen} />
      <Tab.Screen name="Sayaç" component={FocusScreen} />
      <Tab.Screen name="Sınav" component={ExamScreen} />
      <Tab.Screen name="Ritim" component={StatsScreen} />
      <Tab.Screen name="Not" component={NotesScreen} />
      <Tab.Screen name="Profil" component={ProfileScreen} />
    </Tab.Navigator>
  );
}

const styles = StyleSheet.create({
  bar: {
    position: 'absolute',
    left: 8,
    right: 8,
    bottom: 8,
    height: 82,
    borderRadius: 30,
    backgroundColor: 'rgba(2,6,23,0.96)',
    borderTopWidth: 0,
    borderWidth: 1,
    borderColor: COLORS.border,
    elevation: 18,
    paddingBottom: 6,
    paddingTop: 8,
  },
  label: { fontSize: 9, fontWeight: '900', marginBottom: 4 },
  iconWrap: {
    width: 34,
    height: 30,
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
  },
  iconActive: {
    backgroundColor: COLORS.primary,
  },
});
