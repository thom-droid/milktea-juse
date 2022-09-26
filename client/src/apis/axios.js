import axios from 'axios';

export const apis = {
  join: async (token) => {
    await axios
      .post(
        `http://juse.iptime.org:8080/users/join`,
        {
          introduction: '용우정입니다',
          portfolio: 'holdmetight',
          nickname: '용우정',
          skillStackTags: ['react', 'java'],
        },
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res.data.data));
  },
  boardPost: async (token) => {
    await axios
      .post(
        `http://juse.iptime.org:8080/boards`,
        {
          title: '첫 번째 게시글',
          backend: 5,
          frontend: 2,
          designer: 1,
          etc: 2,
          people: 10,
          contact: 'contact',
          dueDate: '2022-09-14',
          startingDate: '2022-10-05',
          period: '3',
          onOffline: 'online',
          content: 'content1',
          type: 'PROJECT',
          tagList: ['java', 'react', 'figma'],
        },
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res.data.data));
  },
  questionPost: async (token) => {
    await axios
      .post(
        `http://juse.iptime.org:8080/questions/1`,
        {
          content: '정신이 있습니까?',
        },
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res.data.data));
  },
  answerPost: async (token) => {
    await axios
      .post(
        `http://juse.iptime.org:8080/answers/1`,
        {
          content: '없습니다',
        },
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res.data.data));
  },
  getBoard1: async (token) => {
    await axios
      .get(`http://juse.iptime.org:8080/boards/1`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res.data.data));
  },
  postApply: async (token) => {
    await axios
      .post(
        `http://juse.iptime.org:8080/applications/1?position=backend`,
        {},
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res.data.data));
  },
  accept: async (token) => {
    await axios
      .patch(
        `http://juse.iptime.org:8080/applications/2`,
        {},
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res.data.data));
  },
  bookmark: async (token) => {
    await axios
      .post(
        `http://juse.iptime.org:8080/bookmarks/1`,
        {},
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res.data.data));
  },
  myjuse: async (token) => {
    await axios
      .get(`http://juse.iptime.org:8080/users/myjuse`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res.data.data));
  },
  mypage: async (token) => {
    await axios
      .get(`http://juse.iptime.org:8080/users/`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res.data.data));
  },
  like: async (token) => {
    await axios
      .post(
        `http://juse.iptime.org:8080/likes/1`,
        {},
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res.data.data));
  },
  dislike: async (token) => {
    await axios
      .delete(`http://juse.iptime.org:8080/likes/1`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res.data.data));
  },
  other: async (token) => {
    await axios
      .get(`http://juse.iptime.org:8080/users/1`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res.data.data));
  },
  bookDelete: async (token) => {
    await axios
      .delete(`http://juse.iptime.org:8080/bookmarks/1`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res));
  },
  update: async (token) => {
    await axios
      .patch(
        `http://juse.iptime.org:8080/boards/1`,
        {
          contact: 'updated contact',
          dueDate: '2022-09-14',
          startingDate: '2022-10-05',
          period: '3',
          onOffline: 'online',
          content: '수정된 게시글',
          title: '또 수정',
          type: 'PROJECT',
          tagList: ['figma'],
        },
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res));
  },
  questionUpdate: async (token) => {
    await axios
      .patch(
        `http://juse.iptime.org:8080/questions/1`,
        {
          content: '수정수정',
        },
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res));
  },
  answerUpdate: async (token) => {
    await axios
      .patch(
        `http://juse.iptime.org:8080/answers/1`,
        {
          content: '답변 수정',
        },
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res));
  },
  answerDelete: async (token) => {
    await axios
      .delete(`http://juse.iptime.org:8080/answers/1`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res));
  },
  questionDelete: async (token) => {
    await axios
      .delete(`http://juse.iptime.org:8080/questions/1`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res));
  },
  myUpdate: async (token) => {
    await axios
      .patch(
        `http://juse.iptime.org:8080/users`,
        {
          introduction: '용우정입니다',
          portfolio: '포폴업뎃',
          nickname: '용정우',
          skillStackTags: ['react', 'java'],
        },
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res));
  },
};