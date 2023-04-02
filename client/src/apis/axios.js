import axios from 'axios';

export const API_BASE_URL  = process.env.REACT_APP_API_ROOT;

export const apis = {
  // 회원가입
  postJoin: async (token, data, img) => {
    const formData = new FormData();
    formData.append(
      'userPostDto',
      new Blob([JSON.stringify(data)], { type: 'application/json' })
    );
    formData.append('profileImg', img);
    await axios
      .post(`${API_BASE_URL}/users/join`, formData, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res))
      .catch((err) => console.log(err));
  },
  getNickname: async (nickname) => {
    return await axios
      .get(`${API_BASE_URL}/users/nicknames?q=${nickname}`)
      .then((res) => res.data.data)
      .catch((err) => console.log(err));
  },

  // 마이페이지
  getUsers: async (token) => {
    return await axios
      .get(`${API_BASE_URL}/users`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => res.data.data)
      .catch((err) => console.log(err));
  },
  getOtherUsers: async (token, userId) => {
    return await axios
      .get(`${API_BASE_URL}/users/${userId}`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => res.data.data)
      .catch((err) => console.log(err));
  },
  deleteUser: async (token) => {
    await axios
      .delete(`${API_BASE_URL}/users`, {
        headers: {
          Auth: token,
        },
      })
      .then(() => {
        alert('정상적으로 탈퇴되었습니다.');
      })
      .catch((err) => console.log(err));
  },

  // 유저 정보 수정
  patchUser: async (token, data, img) => {
    const formData = new FormData();
    formData.append(
      'patchDto',
      new Blob([JSON.stringify(data)], { type: 'application/json' })
    );
    formData.append('profileImg', img);
    await axios
      .patch(`${API_BASE_URL}/users`, formData, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res))
      .catch((err) => console.log(err));
  },

  // 게시물 상세
  getBoardDetail: async (token, boardId) => {
    return await axios
      .get(`${API_BASE_URL}/boards/${boardId}`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => res.data.data)
      .catch((err) => console.log(err));
  },

  // 질문 게시 수정 삭제
  postQuestion: async (token, data, boardId) => {
    await axios
      .post(`${API_BASE_URL}/questions/${boardId}`, data, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res.data.data));
  },
  patchQuestion: async (token, data, questionId) => {
    await axios
      .patch(
        `${API_BASE_URL}/questions/${questionId}`,
        data,
        {
          headers: {
            Auth: token,
          },
        }
      )
      .catch((err) => console.log(err));
  },
  deleteQuestion: async (token, questionId) => {
    await axios
      .delete(`${API_BASE_URL}/questions/${questionId}`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res));
  },

  // 답변 게시 수정 삭제
  postAnswer: async (token, data, questionId) => {
    await axios
      .post(`${API_BASE_URL}/answers/${questionId}`, data, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res.data.data));
  },
  patchAnswer: async (token, data, answerId) => {
    await axios
      .patch(`${API_BASE_URL}/answers/${answerId}`, data, {
        headers: {
          Auth: token,
        },
      })
      .catch((err) => console.log(err));
  },
  deleteAnswer: async (token, answerId) => {
    await axios
      .delete(`${API_BASE_URL}/answers/${answerId}`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res));
  },

  // 마이주씨
  getMyjuse: async (token) => {
    return await axios
      .get(`${API_BASE_URL}/users/myjuse`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => res.data.data)
      .catch((err) => console.log(err));
  },

  // 게시물 작성
  postBoard: async (token, data) => {
    await axios
      .post(`${API_BASE_URL}/boards`, data, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res.data.data))
      .catch((err) => console.log(err));
  },

  // 게시물 수정
  patchBoard: async (token, data, boardId) => {
    await axios
      .patch(`${API_BASE_URL}/boards/${boardId}`, data, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res))
      .catch((err) => console.log(err));
  },

  // 게시물 삭제
  deleteBoard: async (token, boardId) => {
    await axios
      .delete(`${API_BASE_URL}/boards/${boardId}`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res, '삭제됨'))
      .catch((err) => console.log(err));
  },

  // 지원기능
  postApply: async (token, boardId, position) => {
    await axios
      .post(
        `${API_BASE_URL}/applications/${boardId}?position=${position}`,
        {},
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res.data.data))
      .then((err) => console.log(err));
  },
  patchAccept: async (token, applicationId) => {
    await axios
      .patch(
        `${API_BASE_URL}/applications/${applicationId}`,
        {},
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res.data.data))
      .catch((err) => console.log(err));
  },
  deleteDeny: async (token, applicationId) => {
    await axios
      .delete(
        `${API_BASE_URL}/applications/${applicationId}`,
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res.data.data))
      .then((err) => console.log(err));
  },

  // 북마크 등록
  postBookmark: async (token, boardId) => {
    return await axios.post(
      `${API_BASE_URL}/bookmarks/${boardId}`,
      {},
      {
        headers: {
          Auth: token,
        },
      }
    );
  },

  // 북마크 삭제
  deleteBookmark: async (token, boardId) => {
    await axios.delete(
      `${API_BASE_URL}/bookmarks/${boardId}`,
      {
        headers: {
          Auth: token,
        },
      }
    );
  },

  // 다른 유저 좋아요
  postLike: async (token, id) => {
    await axios
      .post(
        `${API_BASE_URL}/likes/${id}`,
        {},
        {
          headers: {
            Auth: token,
          },
        }
      )
      .then((res) => console.log(res.data.data));
  },

  // 다른 유저 좋아요 삭제
  deleteLike: async (token, id) => {
    await axios
      .delete(`${API_BASE_URL}/likes/${id}`, {
        headers: {
          Auth: token,
        },
      })
      .then((res) => console.log(res.data.data));
  },

  // 게시물 목록 조회 및 필터링, 무한스크롤
  getBoards: async (token, type, tag, period, status, page) => {
    const res = await axios.get(
      `${API_BASE_URL}/boards?page=${page}&type=${type.toUpperCase()}${
        tag.length ? '&tag=' + tag.join(',') : ''
      }${
        period.length ? '&period=' + period.join(',') : ''
      }&status=${status.toUpperCase()}`,
      {
        headers: {
          Auth: token,
        },
      }
    );
    const { data, pagination } = res.data;
    const isLast =
      pagination.page === pagination.totalPages || pagination.totalPages === 0;
    return { data, isLast, nextPage: page + 1 };
  },

  getNotifications: async (token) => {
      const response = await axios.get()
  }
};
