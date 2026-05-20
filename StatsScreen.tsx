import React from 'react';
import { StyleSheet, View, ViewStyle } from 'react-native';
import { COLORS } from '../../theme/colors';

export default function GlassCard({children,style}:{children:React.ReactNode;style?:ViewStyle}) {
  return <View style={[styles.card,style]}>{children}</View>;
}
const styles = StyleSheet.create({
  card:{backgroundColor:COLORS.card,borderWidth:1,borderColor:COLORS.border,borderRadius:30,padding:18,
  shadowColor:'#000',shadowOffset:{width:0,height:18},shadowOpacity:.28,shadowRadius:28,elevation:10}
});
