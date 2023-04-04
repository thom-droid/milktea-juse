import {useEffect, useRef, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import styled, {keyframes} from 'styled-components';
import {DarkThemeBtn, LightThemeBtn, Logo, NavbarContainer, NavbarSubContainer, NavButtons,} from './NavbarPublic';
import {ReactComponent as NotificationIcon} from '../assets/icons/notification.svg';
import {ReactComponent as NotificationAlertIcon} from '../assets/icons/notification-alert.svg';
import {ReactComponent as UserIcon} from '../assets/icons/user.svg';
import {ReactComponent as BookmarkIcon} from '../assets/icons/bookmark.svg';
import {ReactComponent as LogoutIcon} from '../assets/icons/logout.svg';
import {ReactComponent as NavigateIcon} from "../assets/icons/navigate-icon-original.svg";
import {useCookies} from 'react-cookie';
import {API_BASE_URL} from '../apis/axios';
import useDetectClose from '../hooks/useDetectClose';
import {ReactComponent as Sun} from '../assets/icons/sun.svg';
import {ReactComponent as Moon} from '../assets/icons/moon.svg';

import axios from 'axios';

const NavbarPrivate = ({ removeCookie, theme, toggleTheme }) => {
  const [cookies] = useCookies();
  const token = cookies.user;
  const uuid = cookies.userUuid;
  const userProfileImage = cookies.userProfileImage;
  const [imageSrc, setImageSrc] = useState('/icons/img/user-default.png');
  const param = useParams();
  const navigate = useNavigate();
  // dropdown 외부 클릭 감지
  const profileDropdownRef = useRef(null);
  const [isProfileDropDownOpen, setProfileDropDownOpen] = useDetectClose(profileDropdownRef, false);

  // notification dropdown box
  const notificationDropDownRef = useRef(null);
  const [isNotificationDropDownOpen, setNotificationDropDownOpen] = useDetectClose(notificationDropDownRef, false);

  //notification list
  const [notificationList, setNotificationList] = useState([]);

  // notification popup modal
  const [notification, setNotification] = useState(null);
  const [notificationModal, setNotificationModal] = useState(false);

  // notification modal setup when new message is made
  useEffect(() => {
    if(notification){
      console.log(`notification: ${notification}`)
      setNotificationModal(true);
      setTimeout(() => {
        setNotificationModal(false);
        setNotification(null);
      }, 10000);
    }
  }, [notification]);

  // set user profile image and connect to event stream
  useEffect(() => {
    setImageSrc(userProfileImage);
    connect(uuid);

    axios.get(`${API_BASE_URL}/notifications/nav`, { headers: { "Auth" : token} } )
        .then((response) => {
            setNotificationList(response.data.data);
        })
        .catch((error) => {
            console.log(error);
    })
  }, []);


  function connectToStream(streamUrl) {
    let request, sse;

    try {
      request = new Request(streamUrl);
      sse = new EventSource(streamUrl);

      sse.onmessage = (evt) => {
        console.log(`received notification : ${evt.data}`);

        try {
          const tmp = JSON.parse(evt.data);
          let url = tmp.relatedURL + '';
          const boardId = url.substring(url.lastIndexOf('/')+1);
          const data = () => {
            return {
              id: tmp.id,
              message: tmp.message,
              boardId: boardId
            };
          }
          setNotification(data);
        } catch(e){
          console.log('this is a test connection.');
        }
      };

      sse.onerror = () => {
        console.log('timed out. Reestablishing the connection...');

        fetch(request)
            .then((response) => {
              const status = response.status;
              console.log(`connection reestablished. ${status}`);
            })
            .catch((error) => {
              console.log(`there is no server running. ${error}`);
              sse.close();
            });
      };
    } catch (error) {
      console.log(error);
    }
  }

  function connect ( uuid ) {
    if(uuid == null || uuid.isEmpty) {
      return null;
    }

    const streamUrl = API_BASE_URL.concat(`/notification/event-stream/${uuid}`);
    connectToStream(streamUrl);
  }

  const handleLogout = () => {
    removeCookie('user', { path: '/' });
    removeCookie('userId', { path: '/' });
    navigate('/');
  };

  return (
      <NavbarContainer>
        <NavbarSubContainer>
          <Logo/>
          <NavButtons>
            {theme === 'light' ? (
                <DarkThemeBtn className='theme-btn' onClick={toggleTheme}>
                  <Moon fill='#ffea00'/>
                </DarkThemeBtn>
            ) : (
                <LightThemeBtn className='theme-btn' onClick={toggleTheme}>
                  <Sun fill='#00e676'/>
                </LightThemeBtn>
            )}
            <Notification
                ref={notificationDropDownRef}
                onClick={() => {
                   setNotificationDropDownOpen(!isNotificationDropDownOpen);
                   console.log(notificationList);
                }}
            >
              {isNotificationDropDownOpen ? (
                notificationList.length === 0 ? (
                <EmptyNotificationDropDown>
                  <p>모든 알림을 읽었습니다.</p>
                </EmptyNotificationDropDown>
                ) : (
                  <NotificationDropDown>
                    {notificationList.map((content, index) => (
                      <NotificationContent to={content.relatedURL} key={index}>
                        <div>
                          <p>{content.message}</p>
                        </div>
                      </NotificationContent>
                    ))}
                  </NotificationDropDown>
                )
              ) : null}
              {notificationList.length === 0 ? (
                  <NotificationIcon
                      width='28px'
                      height='28px'
                  />
              ) : (
                  <NotificationAlertIcon
                      width='28px'
                      height='28px'
                  />
              )}
            </Notification>
            <Profile
                ref={profileDropdownRef}
                onClick={() => {
                  setProfileDropDownOpen(!isProfileDropDownOpen);
                }}>
              <img src={imageSrc} alt='profile'/>
              {isProfileDropDownOpen ? (
                  <ProfileDropdownNav>
                    <ProfileDropdownLink to='/users'>
                      <UserIcon/>
                      <p>마이페이지</p>
                    </ProfileDropdownLink>
                    <ProfileDropdownLink to='/users/myjuse'>
                      <BookmarkIcon/>
                      <p>나의 JUSE</p>
                    </ProfileDropdownLink>
                    <ProfileDropdownLink to='/' onClick={handleLogout}>
                      <LogoutIcon/>
                      <p>로그아웃</p>
                    </ProfileDropdownLink>
                  </ProfileDropdownNav>
              ) : (
                  ''
              )}
            </Profile>
          </NavButtons>
        </NavbarSubContainer>
        {notification && notificationModal && (
            <NotificationModal hidden={!notificationModal} to={`boards/${notification.boardId}`}>
              <NavigateIcon width='16px' height='16px'/>
              <p>{notification.message}</p>
            </NotificationModal>
        )}
      </NavbarContainer>
  );
};

const Notification = styled.div`
  font-size: 18px;
  color: ${({ theme }) => theme.colors.grey4};
  display: flex;
  align-items: center;
  cursor: pointer;
  :hover {
    color: ${({ theme }) => theme.colors.purple1};
  }
`;

const NotificationModal = styled(Link)`
  width: 300px;
  height: 75px;
  text-align: center;
  position: fixed;
  bottom: 10px;
  right: 0px;
  justify-content: space-between;
  transform: translate(-50%, -50%);
  background-color: ${({ theme }) => theme.colors.purple1};
  padding: 30px;
  opacity: 1;
  font-size: 18px;
  animation: ${({ hidden }) => hidden ? fadeOut : fadeIn} 0.5s ease-in-out;
  border-radius: 8px;
  color: white;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: space-evenly;
  fill: white;

  :hover {
    font-weight: 600;
  }
`;

const NotificationDropDown = styled.nav`
  background: ${({theme}) => theme.background};
  border-radius: 8px;
  position: absolute;
  top: 80px;
  right: 0px;
  width: 500px;
  box-shadow: 0 1px 8px ${({theme}) => theme.colors.grey2};
  z-index: 999;
`;

const EmptyNotificationDropDown = styled.div`
  background: ${({theme}) => theme.background};
  border-radius: 8px;
  position: absolute;
  top: 80px;
  right: 0px;
  width: 350px;
  box-shadow: 0 1px 8px ${({theme}) => theme.colors.grey2};
  color: black;
  z-index: 999;
  padding: 10px 10px;
`;

const NotificationContent = styled(Link)`
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid ${({ theme }) => theme.colors.grey2};
  padding: 22px 20px;
  font-size: 14px;
  color: ${({ theme }) => theme.colors.black1};
  > i {
    margin-right: 10px;
  }
  :hover {
    color: ${({ theme }) => theme.colors.purple1};
  }
`;

const Profile = styled.div`
  position: relative;
  width: 32px;
  height: 32px;
  border: 1px solid ${({ theme }) => theme.colors.grey3};
  border-radius: 50px;
  cursor: pointer;
  > img {
    position: absolute;
    top: 0;
    left: 0;
    transform: translate(50, 50);
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 999px;
    margin: auto;
    padding: 2px;
  }
  :hover {
    border: 1px solid ${({ theme }) => theme.colors.purple1};
  }
`;

const ProfileDropdownNav = styled.nav`
  background: ${({ theme }) => theme.background};
  border-radius: 8px;
  position: absolute;
  top: 60px;
  right: 0;
  width: 140px;
  box-shadow: 0 1px 8px ${({ theme }) => theme.colors.grey2};
  z-index: 999;
`;

const ProfileDropdownLink = styled(Link)`
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid ${({ theme }) => theme.colors.grey2};
  padding: 15px 20px;
  font-size: 14px;
  color: ${({ theme }) => theme.colors.black1};
  > i {
    margin-right: 10px;
  }
  :hover {
    color: ${({ theme }) => theme.colors.purple1};
  }
`;


const fadeIn = keyframes`
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
`;

const fadeOut = keyframes`
  from {
    opacity: 1;
  }
  to {
    opacity: 0;
  }
`;

export default NavbarPrivate;
