import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { COLORS } from '../theme/colors';
import TimerScreen from '../screens/TimerScreen';
import StatsScreen from '../screens/StatsScreen';
import TasksScreen from '../screens/TasksScreen';
import CoachScreen from '../screens/CoachScreen';
import ExamScreen from '../screens/ExamScreen';
import RhythmScreen from '../screens/RhythmScreen';
import SettingsScreen from '../screens/SettingsScreen';

const Tab = createBottomTabNavigator();

export default function BottomTabs() {
  return (
    <Tab.Navigator
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          backgroundColor: COLORS.cardSolid,
          borderTopColor: COLORS.border,
        },
        tabBarActiveTintColor: COLORS.primary,
        tabBarInactiveTintColor: COLORS.muted,
      }}
    >
      <Tab.Screen name='Koc' component={CoachScreen} />
      <Tab.Screen name='Gorev' component={TasksScreen} />
      <Tab.Screen name='Sayac' component={TimerScreen} />
      <Tab.Screen name='Istatistik' component={StatsScreen} />
      <Tab.Screen name='Sinav' component={ExamScreen} />
      <Tab.Screen name='Ritim' component={RhythmScreen} />
      <Tab.Screen name='Ayarlar' component={SettingsScreen} />
    </Tab.Navigator>
  );
}
