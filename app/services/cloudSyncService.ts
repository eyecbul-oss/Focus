export async function loadCloudData(uid: string) {
  return null;
}

export async function saveCloudData(uid: string, data: any) {
  return true;
}

export function mergeLatest(local: any, cloud: any) {
  return cloud || local;
}
