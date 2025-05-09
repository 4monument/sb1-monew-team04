import http from 'k6/http';
import {check, group, sleep} from 'k6';

export const options = {
  vus: 400,              // 동시 사용자 수
  duration: '30s',      // 테스트 시간
};

const BASE_URL = 'http://192.168.0.101:8080/api/user-activities';

const userIds = [
  "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
  "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
  "cccccccc-cccc-cccc-cccc-cccccccccccc",
  "dddddddd-dddd-dddd-dddd-dddddddddddd"
];

// ⚙️ 최초 실행: save 요청 보내기 (1회)
export function setup() {
  for (const userId of userIds) {
    const url = `${BASE_URL}/${userId}/save`;
    const res = http.get(url, null);  // params 없음
    console.log(`Saved activity for ${userId} => status: ${res.status}`);
  }
}

export default function () {

  group('조회 테스트', () => {
    for (const userId of userIds) {
      const url = `${BASE_URL}/${userId}/query`;
      const res = http.get(url);

      const success = check(res, {
        'status is 200': (r) => r.status === 200,
      });

      if (!success) {
        console.error(
            `❌ userId=${userId}, status=${res.status}, body=${res.body}`);
      }

      sleep(0.005); // 요청 간 간격
    }
  });
}
