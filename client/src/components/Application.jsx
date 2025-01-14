import { useCookies } from 'react-cookie';
import { Link } from 'react-router-dom';
import styled from 'styled-components';
import { apis } from '../apis/axios';
import { ReactComponent as Accept } from '../assets/icons/check-circle.svg';
import { ReactComponent as Deny } from '../assets/icons/x-circle.svg';
import theme from '../assets/styles/Theme';
import { useMutation, useQueryClient } from 'react-query';
import {useEffect} from 'react';

const Application = ({ data }) => {
  const [cookies] = useCookies();
  const token = cookies.user;
  const myId = +cookies.userId;
  const queryClient = useQueryClient();

  // 포지션 별 지원 현황 정리
  // count : 포지션 별 TO , accepted: 확정된 인원 , pending: 보류 중 인원
  const positions = [
    {
      position: '프론트엔드',
      value: 'frontend',
      count: data.frontend,
      accepted: data.applicationList.filter(
        (e) => e.position === 'frontend' && e.status === 'ACCEPTED'
      ),
      pending: data.applicationList.filter(
        (e) => e.position === 'frontend' && e.status === 'ON_WAIT'
      ),
    },
    {
      position: '백엔드',
      value: 'backend',
      count: data.backend,
      accepted: data.applicationList.filter(
        (e) => e.position === 'backend' && e.status === 'ACCEPTED'
      ),
      pending: data.applicationList.filter(
        (e) => e.position === 'backend' && e.status === 'ON_WAIT'
      ),
    },
    {
      position: '디자이너',
      value: 'designer',
      count: data.designer,
      accepted: data.applicationList.filter(
        (e) => e.position === 'designer' && e.status === 'ACCEPTED'
      ),
      pending: data.applicationList.filter(
        (e) => e.position === 'designer' && e.status === 'ON_WAIT'
      ),
    },
    {
      position: '기타',
      value: 'etc',
      count: data.etc,
      accepted: data.applicationList.filter(
        (e) => e.position === 'etc' && e.status === 'ACCEPTED'
      ),
      pending: data.applicationList.filter(
        (e) => e.position === 'etc' && e.status === 'ON_WAIT'
      ),
    },
    {
      position: '스터디원',
      value: 'people',
      count: data.people,
      accepted: data.applicationList.filter(
        (e) => e.position === 'people' && e.status === 'ACCEPTED'
      ),
      pending: data.applicationList.filter(
        (e) => e.position === 'people' && e.status === 'ON_WAIT'
      ),
    },
  ];

  // 지원하기
  const clickApply = (el) => {
    if (token) {
      if (window.confirm(`${el.position}에 지원하시겠습니까?`)) {
        apis
          .postApply(token, data.id, el.value)
          .then(() => alert(`${el.position}에 지원을 완료했습니다.`))
          .catch(() => alert('하나의 모집 글에는 한 번의 지원만 가능합니다.'));
      }
    } else {
      alert('로그인이 필요한 작업입니다.');
    }
    return;
  };

  // 지원 관리
  const acceptMutation = useMutation(
    (applicationId) => apis.patchAccept(token, applicationId),
    {
      onSuccess: (data, variable, context) => {
        setTimeout(() => {
          queryClient.invalidateQueries('board');
        }, 200);
      },
    }
  );

  const denyMutation = useMutation(
    (applicationId) => apis.deleteDeny(token, applicationId),
    {
      onSuccess: (data, variable, context) => {
        setTimeout(() => {
          queryClient.invalidateQueries('board');
        }, 200);
      },
    }
  );

  // 지원 하기, 지원 완료 버튼 교체
  const buttonSwitch = (el) => {
    const history = data.applicationList.filter((e) => e.userId === myId);
    if (history.length) {
      return el.value === history[0].position ? (
        <ApplyButton className='closed applied'>지원 완료</ApplyButton>
      ) : (
        <ApplyButton className='closed'>지원</ApplyButton>
      );
    } else {
      return (
        <ApplyButton
          onClick={() => {
            clickApply(el);
          }}>
          지원
        </ApplyButton>
      );
    }
  };

  return (
    <ApplicationContainer>
      <StatusContainer>
        <SubTitle>지원 현황</SubTitle>
        <PositionsContainer>
          {positions.map((el, i) =>
            el.count ? (
              <div key={i}>
                <Position>
                  <div className='position-name'>{el.position}</div>
                  <div className='count'>{`${el.accepted.length} / ${el.count}`}</div>
                  {!data.auth ? (
                    el.count === el.accepted.length ? (
                      <ApplyButton className='closed'>마감</ApplyButton>
                    ) : (
                      buttonSwitch(el)
                    )
                  ) : (
                    ''
                  )}
                </Position>
                {data.auth ? (
                  <PendingContainer>
                    <span>지원자</span>
                    {el.pending.length ? (
                      el.pending.map((apply, i) => (
                        <PendingBubble key={i} isAccepted={false}>
                          <Link to={`/users/${apply.userId}`}>
                            {apply.nickname}
                          </Link>
                          <Accept
                            fill={theme.colors.purple1}
                            onClick={() => {
                              acceptMutation.mutate(apply.id);
                            }}
                          />
                          <Deny
                            fill={theme.colors.grey4}
                            onClick={() => {
                              denyMutation.mutate(apply.id);
                            }}
                          />
                        </PendingBubble>
                      ))
                    ) : (
                      <p className='null-message'>지원자가 없습니다.</p>
                    )}
                  </PendingContainer>
                ) : (
                  ''
                )}
              </div>
            ) : (
              ''
            )
          )}
        </PositionsContainer>
      </StatusContainer>
      {data.auth ? (
        <StatusContainer>
          <SubTitle>팀원 현황</SubTitle>
          {positions.map((el, i) =>
            el.count ? (
              <AcceptedContainer key={i}>
                {el.accepted.length ? (
                  el.accepted.map((apply) => (
                    <PendingBubble isAccepted={true}>
                      <Link to={`/users/${apply.userId}`}>
                        {apply.nickname}
                      </Link>
                      <Deny
                        fill={theme.colors.grey4}
                        onClick={() => {
                          denyMutation.mutate(apply.id);
                        }}
                      />
                    </PendingBubble>
                  ))
                ) : (
                  <p className='null-message'>모집된 팀원이 없습니다.</p>
                )}
              </AcceptedContainer>
            ) : (
              ''
            )
          )}
        </StatusContainer>
      ) : (
        ''
      )}
    </ApplicationContainer>
  );
};

const SubTitle = styled.h4`
  font-weight: 700;
  font-size: 22px;
  margin: 5px 0 20px 0;
`;

const ApplicationContainer = styled.div`
  display: flex;
  padding: 15px 0;
  border-bottom: 1px solid ${({ theme }) => theme.colors.grey2};
`;

const StatusContainer = styled.div`
  width: 50%;
`;

const PositionsContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
`;

const Position = styled.div`
  display: flex;
  align-items: center;
  font-size: 18px;
  > .position-name {
    font-weight: 600;
    width: 150px;
  }
  > .count {
    width: 60px;
  }
`;

const ApplyButton = styled.button`
  padding: 5px 10px;
  background: ${({ theme }) => theme.background};
  font-size: 14px;
  color: ${({ theme }) => theme.colors.purple1};
  border: 1px solid ${({ theme }) => theme.colors.purple1};
  border-radius: 4px;
  cursor: pointer;
  :hover {
    color: #ffffff;
    border: 1px solid ${({ theme }) => theme.colors.purple1};
    background: ${({ theme }) => theme.colors.purple1};
  }
  &.closed {
    background-color: ${({ theme }) => theme.colors.grey2};
    pointer-events: none;
  }
  &.applied {
    border: 1px solid ${({ theme }) => theme.colors.purple1};
    color: ${({ theme }) => theme.colors.purple1};
  }
`;

const AcceptedContainer = styled.div`
  display: flex;
  gap: 15px;
  height: 76px;
  margin-bottom: 20px;
  :last-child {
    margin-bottom: 5px;
  }
  > .null-message {
    color: ${({ theme }) => theme.colors.grey4};
    font-size: 15px;
  }
`;

const PendingContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 15px;
  margin: 15px 0 5px 0;
  > .null-message {
    display: flex;
    align-items: center;
    color: ${({ theme }) => theme.colors.grey4};
    font-size: 15px;
    height: 38px;
  }
`;

const PendingBubble = styled.div`
  display: flex;
  gap: 5px;
  padding: 10px;
  border: 1px solid
    ${({ theme, isAccepted }) =>
      isAccepted ? theme.colors.purple1 : theme.colors.grey2};
  border-radius: 999px;
  height: 38px;
  > svg {
    cursor: pointer;
  }
`;

export default Application;
