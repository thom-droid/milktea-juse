import styled from 'styled-components';
import google from '../assets/images/google.svg';
import { ReactComponent as CloseBtnIcon } from '../assets/icons/close.svg';
import { ReactComponent as Github } from '../assets/images/github.svg';
import theme from '../assets/styles/Theme';

export const OAuth_API_ROOT = process.env.REACT_APP_API_ROOT + "/oauth2/authorization";
export const Google_URL = `${OAuth_API_ROOT}/google`;
export const Github_URL = `${OAuth_API_ROOT}/github`;

const LoginModal = ({ showModal, setShowModal }) => {
  const closeModal = () => {
    setShowModal(!showModal);
  };

  return (
    <>
      {showModal ? (
        <Background onClick={closeModal}>
          <LoginModalContainer showModal={showModal}>
            <CloseBtn onClick={closeModal}>
              <CloseBtnIcon />
            </CloseBtn>
            <p>환영합니다</p>
            <GoogleLoginBtn href = {Google_URL}>
              <GoogleLogo />
              <p>Google 계정으로 로그인</p>
            </GoogleLoginBtn>
            <GoogleLoginBtn href= {Github_URL}>
              <Github
                width={'18px'}
                height={'18px'}
                style={{ 'margin-right': '10px' }}
              />
              <p>Github 계정으로 로그인</p>
            </GoogleLoginBtn>
          </LoginModalContainer>
        </Background>
      ) : null}
    </>
  );
};

// Login Modal이 열렸을 때 뒷 배경
const Background = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 999;
  background-color: rgba(0, 0, 0, 0.1);
`;

const LoginModalContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  align-items: center;
  position: relative;
  width: 500px;
  height: 300px;
  background: ${({ theme }) => theme.background};
  border-radius: 4px;
  box-shadow: 0 0 10px 3px rgba(0, 0, 0, 0.1);
  > p {
    font-size: 20px;
    font-weight: 600;
    color: ${({ theme }) => theme.text};
  }
`;

const CloseBtn = styled.div`
  position: absolute;
  top: 15px;
  right: 15px;
  color: ${({ theme }) => theme.colors.grey2};
  cursor: pointer;
  :hover {
    color: ${({ theme }) => theme.colors.purple1};
  }
`;

const GoogleLogo = styled.img.attrs({
  src: `${google}`,
})`
  width: 18px;
  height: 18px;
  margin-right: 10px;
`;

const GoogleLoginBtn = styled.a`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 300px;
  padding: 10px 20px;
  border: 1px solid ${({ theme }) => theme.colors.grey2};
  border-radius: 4px;
  cursor: pointer;
  :hover {
    border: 1px solid ${({ theme }) => theme.colors.purple1};
  }
  > p {
    color: ${({ theme }) => theme.colors.black1};
    font-size: 14px;
  }
`;

export default LoginModal;
