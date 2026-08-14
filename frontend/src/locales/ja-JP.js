export default {
  common: {
    confirm: '確認', cancel: 'キャンセル', save: '保存', submit: '送信', close: '閉じる',
    loading: '読み込み中…', noData: 'データがありません', retry: '再試行', back: '戻る', backToTop: 'ページ上部へ戻る',
    search: '検索', refresh: '更新', delete: '削除'
  },
  music: {
    label: '音楽', title: 'DevLog BGM',
    expand: 'プレーヤーを開く', collapse: 'プレーヤーを閉じる',
    playerLabel: 'DevLog グローバル音楽プレーヤー', emptyPlaylist: 'プレイリストが未設定です', unknownArtist: '不明なアーティスト',
    noLyrics: '歌詞はありません', loadFailed: '曲を読み込めませんでした', coverAlt: '{title} のカバー',
    seek: '再生位置', previous: '前の曲', play: '再生', pause: '一時停止', next: '次の曲'
  },
  seasonEffect: {
    label: '季節エフェクト', shortLabel: '季節', none: 'エフェクトなし',
    sakura: '春・桜', rain: '夏・雨', leaf: '秋・落葉', snow: '冬・雪',
    reducedMotion: '端末の「視差効果を減らす」設定により季節アニメーションを停止しています'
  },
  nav: {
    media: '作品記録', notes: 'ノート',
    home: 'ホーム', archive: 'アーカイブ', about: 'このサイトについて', login: 'ログイン',
    logout: 'ログアウト', admin: '管理画面', profile: 'プロフィール',
    changePassword: 'パスワード変更', search: '検索', userMenu: 'ユーザーメニュー',
    user: 'ユーザー', avatar: 'ユーザーアイコン', noBio: 'プロフィールはまだありません'
  },
  home: {
    latestArticles: '最新の記事', all: 'すべて', articles: '記事', categories: 'カテゴリー',
    comments: 'コメント', clock: '時計', announcement: 'お知らせ',
    noAnnouncement: 'お知らせはありません', randomArticles: 'おすすめ記事',
    searchPlaceholder: '記事・概要・タグを検索…', currentStatus: '現在のステータス', noQuote: '思考と日々の記録。',
    noFeaturedPost: 'おすすめの記事はまだありません', browseArchive: 'アーカイブを見る', archiveHint: 'タイムラインから続きを読む', themeHint: 'ライト・ダークを切り替え'
  },
  article: {
    views: '閲覧', comments: 'コメント', words: '文字', publishedAt: '公開日時',
    updatedAt: '更新日時', noSummary: 'この記事にはまだ概要がありません。',
    readMore: '続きを読む', backHome: 'ホームへ戻る', toc: '目次',
    noToc: '目次はありません', related: '関連記事', noRelated: '関連記事はありません',
    noArticles: '記事はありません', previous: '前へ', next: '次へ',
    uncategorized: '未分類', statistics: '記事の統計'
  },
  archive: {
    title: 'アーカイブ',
    total: '全 {count} 件の記事', articleUnit: '件の記事', search: 'アーカイブを検索',
    searchPlaceholder: 'タイトルまたは概要を検索…', clearSearch: '検索をクリア', viewMode: '表示形式',
    timeline: 'タイムライン', grid: 'グリッド', monthLabel: '月', categoryFilter: 'カテゴリーで絞り込む', allCategories: 'すべてのカテゴリー',
    showing: '{count} 件の記事を表示中', empty: '該当する記事はありません',
    emptyHint: '別のキーワードやカテゴリーをお試しください。', loadFailed: 'アーカイブを読み込めませんでした'
  },
  notes: {
    title: 'ノート',
    description: 'プロジェクト開発、コンピューター学習、日常生活、節目の思考を記録します。',
    readMore: '続きを読む', empty: 'まだノートはありません', loadFailed: '読み込みに失敗しました',
    published: '公開日', views: '閲覧', categoryMissing: '「ノート」カテゴリーがまだ初期化されていません。'
  },
  adminTag: {
    menu: 'タグ管理', title: 'タグ管理', subtitle: '記事のトピックタグと使用件数を管理します。',
    create: 'タグを追加', edit: '編集', name: 'タグ名', namePlaceholder: 'タグ名を入力してください',
    articleCount: '関連記事数', createdAt: '作成日時', updatedAt: '更新日時', actions: '操作',
    empty: 'タグはありません', deleting: '削除中…', deleteTitle: 'タグを削除',
    deleteConfirm: 'タグ「{name}」を削除しますか？', nameRequired: 'タグ名を入力してください',
    nameTooLong: 'タグ名は50文字以内で入力してください', loadFailed: 'タグを読み込めませんでした',
    createSuccess: 'タグを追加しました', updateSuccess: 'タグを更新しました', deleteSuccess: 'タグを削除しました',
    createFailed: 'タグを追加できませんでした', updateFailed: 'タグを更新できませんでした', deleteFailed: 'タグを削除できませんでした',
    selectLabel: 'タグ', selectPlaceholder: 'タグを選択', selectCategoryFirst: '先にカテゴリーを選択してください', optionsFailed: 'タグ候補を読み込めませんでした',
    noOptions: '先にタグ管理でタグを追加してください', noCategoryOptions: 'このカテゴリーに関連付けられたタグはありません'
  },
  tagFilter: {
    label: 'タグで絞り込む', placeholder: 'タグを選択してください', selected: '{count} 個のタグを選択中',
    clear: 'タグをクリア', empty: '条件に一致する記事はありません'
  },
  auth: {
    login: 'ログイン', register: '新規登録', username: 'ユーザー名', email: 'メールアドレス',
    password: 'パスワード', confirmPassword: 'パスワード確認',
    welcomeBack: 'おかえりなさい。アカウントにログインしてください',
    createAccount: '新しいアカウントを作成して、記録を始めましょう',
    forgotPassword: 'パスワードをお忘れですか？', noAccount: 'アカウントをお持ちでないですか？',
    createOne: '新規登録', hasAccount: 'すでにアカウントをお持ちですか？',
    loginNow: 'ログイン', usernamePlaceholder: 'ユーザー名を入力してください',
    emailPlaceholder: 'メールアドレスを入力してください',
    passwordPlaceholder: 'パスワードを入力してください',
    confirmPasswordPlaceholder: 'パスワードをもう一度入力してください',
    lengthHint: '5〜16文字（空白不可）', requiredUsername: 'ユーザー名を入力してください',
    requiredEmail: 'メールアドレスを入力してください',
    invalidEmail: '有効なメールアドレスを入力してください',
    requiredPassword: 'パスワードを入力してください',
    requiredConfirmPassword: 'パスワードをもう一度入力してください',
    invalidLength: '5〜16文字で入力してください', passwordMismatch: 'パスワードが一致しません',
    loginSuccess: 'ログインしました', loginFailed: 'ログインできませんでした',
    requestFailed: 'リクエストに失敗しました。もう一度お試しください',
    registering: '登録中…', formInvalid: '登録内容を確認して修正してください',
    registerFailed: '登録に失敗しました。しばらくしてから再度お試しください',
    registerSuccess: '登録が完了しました。ログインしてください'
  },
  profile: {
    title: 'アカウント情報', hint: 'ログイン中のアカウントのアイコンとニックネームを変更します',
    nickname: 'ニックネーム', nicknameRequired: 'ニックネームを入力してください', nicknameTooLong: 'ニックネームは30文字以内で入力してください',
    username: 'ユーザー名', email: 'メールアドレス', save: '変更を保存',
    avatarHint: 'ブログの公開プロフィールとは別の画像です。アップロード後に変更を保存してください。',
    loginAvatar: 'ログイン用アイコン', changeAvatar: 'アイコンを変更', avatarUploading: 'アップロード中…',
    avatarTypeInvalid: 'JPG、PNG、WebP のみ使用できます',
    avatarSizeInvalid: '画像サイズは 2MB 以下にしてください',
    avatarResolutionLow: '画像が不鮮明になる可能性があります。200 × 200 以上を推奨します',
    avatarUploadSuccess: 'アップロードしました。アカウント情報を保存してください',
    avatarUploadFailed: '画像のアップロードに失敗しました', avatarSaveSuccess: 'アイコンを保存しました',
    avatarSaveFailed: 'アイコンの保存に失敗しました', saveSuccess: 'アカウント情報を保存しました',
    saveFailed: 'アカウント情報の保存に失敗しました', loadFailed: 'アカウント情報の読み込みに失敗しました'
  },
  comment: {
    title: 'コメント', refresh: '更新', replyTo: '@{name}さんへ返信', cancelReply: '返信をキャンセル',
    inputStyle: '入力スタイル', fontSize: '文字サイズ', font: 'フォント',
    small: '小', medium: '中', large: '大', sans: 'ゴシック体', serif: '明朝体', mono: '等幅',
    deviceOnly: '入力スタイルはこの端末にのみ保存されます', loginToJoin: 'ログインしてコメントする',
    shortcut: 'Ctrl + Enter で投稿 · {count}/1000', loginToComment: 'ログインしてコメントする',
    publishing: '投稿中…', publishReply: '返信を投稿', publish: 'コメントを投稿',
    loading: 'コメントを読み込み中…', reload: '再読み込み', emptyTitle: 'コメントはまだありません',
    emptyHint: '最初のコメントを投稿してみませんか？',
    placeholder: '感想やアイデアを気軽に共有してください…',
    replyPlaceholder: '@{name}さんに返信…', loginRequired: '返信するにはログインしてください',
    published: 'コメントを投稿しました', publishFailed: 'コメントを投稿できませんでした',
    loadFailed: 'コメントを読み込めませんでした。しばらくしてからお試しください',
    deleteTitle: 'コメントを削除', deleteMessage: '削除したコメントは元に戻せません。削除しますか？',
    confirmDelete: '削除する', deleted: 'コメントを削除しました',
    deleteFailed: '削除できませんでした', reply: '返信', delete: '削除',
    deactivatedUser: '退会したユーザー', avatarAlt: '{name}さんのアイコン'
  },
  message: {
    loadFailed: '読み込みに失敗しました', operationSuccess: '操作が完了しました',
    operationFailed: '操作に失敗しました', loginRequired: 'ログインしてください',
    loggedOut: 'ログアウトしました'
  },
  language: { label: '言語', chinese: '中文', japanese: '日本語', english: 'English' },
  theme: { toLight: 'ライトモードに切り替え', toDark: 'ダークモードに切り替え' },
  footer: { builtWith: 'Spring Boot + Vue 3 で構築', location: '東京 🌸' },
  about: {
    aboutMe: 'プロフィール', introPending: '（自己紹介は準備中です）',
    techStack: '技術スタック', contactMe: 'お問い合わせ',
    role: 'Java バックエンドエンジニア · 東京'
  },
  siteBackground: {
    menu: '背景管理', title: 'サイト背景画像', subtitle: 'サイト全体のカルーセルで使用する背景画像を管理します。', filters: '背景フィルター',
    create: '背景を追加', edit: '編集', actions: '操作', createTitle: '背景を追加', editTitle: '背景を編集',
    image: '背景画像', backendTitle: '管理用名称', enabledLabel: '有効状態', sortOrder: '並び順の重み',
    sortHint: '数値が大きいほど先に表示されます', uploadImage: '画像をアップロード', replaceImage: '画像を変更', preview: '画像プレビュー',
    previewFailed: '画像をプレビューできません', enabled: '有効', disabled: '無効', allStatus: 'すべての状態',
    searchPlaceholder: '名称で検索', imageUrlPlaceholder: '画像をアップロードするかURLを入力', titlePlaceholder: '任意・管理画面での識別用',
    createdAt: '作成日時', updatedAt: '更新日時', saveSuccess: '保存しました', saveFailed: '保存できませんでした',
    deleteSuccess: '削除しました', deleteFailed: '削除できませんでした', deleteTitle: '背景を削除',
    deleteConfirm: '背景「{title}」を削除しますか？', deleteOssNotice: 'レコードを削除してもアップロード済みファイルは自動削除されません。',
    empty: '背景画像はありません', loadFailed: '背景一覧を読み込めませんでした', detailFailed: '背景詳細を読み込めませんでした',
    updateStatusFailed: '状態を更新できなかったため元に戻しました', uploadSuccess: '画像をアップロードしました', uploadFailed: 'アップロードに失敗しました',
    imageInvalid: '5MB以内の画像ファイルを選択してください',
    validation: { imageRequired: '背景画像をアップロードするかURLを入力してください', imageLength: '背景画像URLは500文字以内です', imageUrl: '有効なHTTP(S)画像URLを入力してください', titleLength: '管理用名称は100文字以内です', enabled: '有効状態は0または1です', sortOrder: '並び順の重みは整数で入力してください', sortRange: '並び順の重みは -100000〜100000 の範囲です' }
  },
  media: {
    title: '作品記録', detailTitle: '作品詳細', subtitle: '読んだ、観た、遊んだ時間を静かに残します。', filters: '作品フィルター', all: 'すべて',
    timeline: 'タイムライン', grid: 'グリッド', viewMode: '表示形式', sort: '並び順', sortLatest: '最近の完了', sortRating: '評価が高い順',
    unrated: '未評価', scoreOutOfTen: '{score}/10 点', noFinishedDate: '未完了', pendingSection: '進行中 / 予定',
    noShortReview: '短評はまだありません', empty: '作品記録はありません', emptyHint: '条件を変えるか、しばらくしてからもう一度ご覧ください。',
    loadFailed: '作品記録を読み込めませんでした', notFound: '作品記録が見つかりません', backToLog: '作品記録へ戻る',
    cover: 'カバー', workTitle: '作品名', typeLabel: '種類', statusLabel: '状態', rating: '評価', shortReview: '短評',
    longReview: '詳細レビュー', finishedDate: '完了日',
    type: { book: '書籍', movie: '映画', anime: 'アニメ', game: 'ゲーム', unknown: '不明' },
    section: { label: '作品の分類', game: 'ゲーム', anime: 'アニメ', movie: '映画', book: '書籍', archive: '趣味の記録', empty: 'この分類にはまだ作品がありません' },
    status: {
      unknown: '不明', generic: { planned: '予定', inProgress: '進行中', completed: '完了', dropped: '中断' },
      book: { planned: '読みたい', inProgress: '読書中', completed: '読了', dropped: '中断' },
      movie: { planned: '観たい', inProgress: '視聴中', completed: '視聴済み', dropped: '中断' },
      anime: { planned: '観たい', inProgress: '視聴中', completed: '視聴済み', dropped: '中断' },
      game: { planned: '遊びたい', inProgress: 'プレイ中', completed: 'クリア済み', dropped: '中断' }
    },
    admin: {
      menu: '作品記録', create: '作品を追加', edit: '編集', actions: '操作', createTitle: '作品を追加', editTitle: '作品を編集',
      searchPlaceholder: 'タイトルで検索', datePlaceholder: '完了日を選択', coverPlaceholder: 'http / https のカバー URL', uploadCover: 'カバーをアップロード',
      ratingHint: '半星 = 1点、五星 = 10点', ratingDisplay: '{stars} 星 / {score} 点', updatedAt: '更新日時', titleRequired: '作品名を入力してください', titleTooLong: '作品名は200文字以内です',
      typeRequired: '種類を選択してください', statusRequired: '状態を選択してください', coverTooLong: 'URLは500文字以内です',
      coverInvalid: 'http / https のURLのみ使用できます', shortReviewTooLong: '短評は500文字以内です', ratingInvalid: '評価は1〜10の整数、または未入力です',
      loadFailed: '作品一覧を読み込めませんでした', detailFailed: '作品詳細を読み込めませんでした', saveSuccess: '保存しました', saveFailed: '保存できませんでした',
      deleteTitle: '作品を削除', deleteConfirm: '「{title}」を削除しますか？', deleteSuccess: '削除しました', deleteFailed: '削除できませんでした',
      imageInvalid: '5MB以内の画像を使用してください', uploadSuccess: 'カバーをアップロードしました', uploadFailed: 'カバーをアップロードできませんでした'
    }
  },
  page: {
    home: 'ホーム', login: 'ログイン', register: '新規登録',
    about: 'このサイトについて', archive: 'アーカイブ', articleDetail: '記事詳細',
    profile: 'プロフィール', changePassword: 'パスワード変更', admin: '管理画面', adminMedia: '作品記録', adminBackground: '背景管理', notes: 'ノート'
  }
}
