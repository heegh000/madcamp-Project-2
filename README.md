# 해적의 고뇌

"해적의 고뇌"는 고전 논리 문제인 [다섯명의 해적](https://namu.wiki/w/%EB%8B%A4%EC%84%AF%20%EB%AA%85%EC%9D%98%20%ED%95%B4%EC%A0%81)에서 착안하여 변칙을 준 게임입니다.

[For game server details](https://github.com/dewpe000/fivePiratesGameServer)

## Account Management

이 앱은 구글 로그인과 로컬 로그인 기능을 통해 User의 ID, NickName, Avatar, Gold, Ranking, History 등을 관리합니다.


### Google Account

처음 로그인 할 경우, 게임 계정 초기화를 위해 Avatar를 선택합니다. 같은 계정으로 다시 로그인할 경우, 이전의 기록을 그대로 유지합니다.


### Local Account (Register & Log in)

TextWatcher 함수를 사용해 조건에 맞지 않는 입력에는 실시간으로 가입/로그인을 차단합니다.

Retrofit2를 사용하여 서버에 입력받은 ID, PW(, Nickname)를 전달해  가입/로그인을 요청합니다.

회원가입 시 Avatar를 선택합니다.

로그인이 성공할 경우 Main Activity가 시작되며, 이전의 Activity는 모두 정리합니다.


<img width="20%" src ="https://user-images.githubusercontent.com/56427889/178464663-4553a446-5681-48bf-be06-9dcb18fb5fc6.png"/> <img width="20%" src ="https://user-images.githubusercontent.com/56427889/178464760-302e9701-4923-4f7f-b4a9-dde88e769b8a.png"/> <img width="20%" src ="https://user-images.githubusercontent.com/56427889/178467118-1ebe52b8-9213-405b-ab77-93f461ac9ea4.png"/> <img width="20%" src ="https://user-images.githubusercontent.com/56427889/178467302-a8681f90-a1bd-496d-b583-a95de90d79a3.png"/> 

<img width="20%" src ="https://user-images.githubusercontent.com/56427889/178467679-0b24ea00-a4a5-402e-b1ce-ae8d85640a5d.png"/> <img width="20%" src ="https://user-images.githubusercontent.com/56427889/178467697-f7d3ca9b-077e-4972-bac2-f7509d2f2144.png"/> <img width="20%" src ="https://user-images.githubusercontent.com/56427889/178479929-0663533f-27c8-4b51-b01f-af0724eea82b.png"/>

## Play Game


### 게임 규칙

>*해적 5명이 금화 1000개가 들어있는 보물상자를 발견했다. 다섯 명의 해적은 랜덤으로 A,B,C,D,E 순으로 순서를 정했다. 모두 합리적으로 판단한다고 했을 때 금화는 어떻게 분배될까? 찬성/반대만 존재하며 기권은 없다. 게임에 입장할 때 가지고 들어온 금화로 뒷거래가 가능하며, 금화를 1개라도 더 얻을 수 있다면 망설임 없이 제안자를 죽이며, 같은 개수일 경우에도 죽이는 것을 선택해 상대의 죽음을 즐길 것이다.*

- 게임을 시작할 때 최대 200금화를 지갑에 넣어갈 수 있습니다. 이 금화는 뒷거래에 사용되며, 게임 중에 사망 시 잃어버립니다.
- 게임이 시작되기 위해서는 다섯 명의 해적이 모여야 합니다. 서버로 대기 중인 player를 보내고, 5명 씩 게임 방이 열립니다.
- 다섯 명의 player에게 임의로 선장>부선장>고급선원>일반선원>잡일꾼의 서열이 부여됩니다. 서열 순으로 금화를 배분합니다. 
- RecyclerView를 통해 다섯 명의 player list를 볼 수 있습니다. 자신은 밝게 표시되어 가독성이 좋습니다. 분배된 금화는 본인만 볼 수 있습니다.
- 투표를 통해 과반수 이상이 동의하면 게임이 종료되고, 각 해적은 자신의 몫의 금화를 얻습니다. 분배가 마음에 들지 않는 경우 반란을 일으켜 분배자를 죽이고, 남은 인원끼리 분배를 다시 시작합니다. 
- 거래를 성공시키고 싶을 떄, 가져온 금화를 찔러줄 수 있습니다. 메세지를 보내보세요.
- 사망 시에도 게임을 관전할 수 있습니다.
- Player가 두 명만 남을 경우에는 해적의 딜레마 게임을 시작합니다.

<img width="20%" src ="https://user-images.githubusercontent.com/56427889/178468637-07a178f2-c210-4db4-813a-030bec0f1f25.png"/> <img width="20%" src ="https://user-images.githubusercontent.com/56427889/178468755-18227385-ed7e-41e5-8e40-b86020ed71a3.png"/> 

<img width="20%" src ="https://user-images.githubusercontent.com/56427889/178479105-cb006585-3059-4ba7-9345-4522a99d3899.png"/> <img width="20%" src ="https://user-images.githubusercontent.com/56427889/178479192-59321a51-9fbd-41d2-a753-635499538e62.png"/> <img width="20%" src ="https://user-images.githubusercontent.com/56427889/178479520-91e28949-13f4-4bdf-997a-c88dc38a21e4.png"/> <img width="20%" src ="https://user-images.githubusercontent.com/56427889/178480115-5db9b13c-2b3d-4cdb-8f97-800ef4c61418.png"/>

<!-- <img width="20%" src =""/><img width="20%" src =""/><img width="20%" src =""/> -->


## Extra Features

### History

그동안의 게임 전적을 볼 수 있습니다. RecyclerView를 통해 구현되었으며, 서버에서 데이터를 받아옵니다.

### Ranking

금화 개수를 기준으로 하여 Player의 랭킹을 볼 수 있습니다. RecyclerView를 통해 구현되었으며, 서버에서 데이터를 받아옵니다.

<img width="20%" src ="https://user-images.githubusercontent.com/56427889/178463162-58cc143c-7c06-441a-a0c4-8fabccca24f3.png"/> <img width = 20% src="https://user-images.githubusercontent.com/56427889/178463173-142ce72d-843d-46cb-8757-f99aea869fdd.png"/>

## Development Setting

- OS: Android (minSdk: 21, targetSdk: 32)
- Language: Java
- IDE: Android Studio
- Target Device: Pixel2

### Author

[한양대학교 컴퓨터소프트웨어학부 한관희](https://github.com/dewpe000)

[KAIST 전산학부 김주연](https://github.com/editadiary)

2022.07.06.-2022.07.12.
