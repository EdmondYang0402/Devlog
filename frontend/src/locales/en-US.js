export default {
  common: {
    confirm: 'Confirm', cancel: 'Cancel', save: 'Save', submit: 'Submit', close: 'Close',
    loading: 'Loading…', noData: 'No data', retry: 'Retry', back: 'Back', backToTop: 'Back to top',
    search: 'Search', refresh: 'Refresh', delete: 'Delete'
  },
  nav: {
    media: 'Media Log', notes: 'Notes',
    home: 'Home', archive: 'Archive', about: 'About', login: 'Log in', logout: 'Log out',
    admin: 'Admin', profile: 'Profile', changePassword: 'Change password',
    search: 'Search', userMenu: 'User menu', user: 'User', avatar: 'User avatar',
    noBio: 'No profile yet'
  },
  home: {
    latestArticles: 'Latest articles', all: 'All', articles: 'Articles', categories: 'Categories',
    comments: 'Comments', clock: 'Clock', announcement: 'Announcement',
    noAnnouncement: 'No announcements', randomArticles: 'More articles',
    searchPlaceholder: 'Search articles, summaries or tags…', currentStatus: 'Current status', noQuote: 'Notes on code and everyday life.',
    noFeaturedPost: 'No featured article yet', browseArchive: 'Browse the archive', archiveHint: 'Continue from the timeline', themeHint: 'Switch light and dark themes'
  },
  article: {
    views: 'Views', comments: 'Comments', words: 'words', publishedAt: 'Published',
    updatedAt: 'Updated', noSummary: 'No summary is available for this article.',
    readMore: 'Read more', backHome: 'Back to home', toc: 'Contents',
    noToc: 'No table of contents', related: 'Related', noRelated: 'No related articles',
    noArticles: 'No articles', previous: 'Previous', next: 'Next',
    uncategorized: 'Uncategorized', statistics: 'Article statistics'
  },
  archive: {
    title: 'Archive',
    total: '{count} articles in total', articleUnit: 'articles', search: 'Search archive',
    searchPlaceholder: 'Search titles or summaries…', clearSearch: 'Clear search', viewMode: 'Archive view',
    timeline: 'Timeline', grid: 'Grid', monthLabel: 'MO', categoryFilter: 'Filter by category', allCategories: 'All categories',
    showing: 'Showing {count} articles', empty: 'No matching articles',
    emptyHint: 'Try another keyword or category.', loadFailed: 'Failed to load the archive'
  },
  notes: {
    title: 'Notes', eyebrow: 'NOTES · ALONG THE WAY',
    subtitle: 'Notes on development, learning, daily life, and thoughts along the way.',
    description: 'Notes on personal projects, computer science, daily life, and reflections.',
    readMore: 'Read more', empty: 'No notes yet', loadFailed: 'Failed to load notes',
    published: 'Published', views: 'Views', categoryMissing: 'The Notes category has not been initialized yet.'
  },
  adminTag: {
    menu: 'Tags', title: 'Tag management', subtitle: 'Manage article topics and their usage counts.',
    create: 'New tag', edit: 'Edit', name: 'Tag name', namePlaceholder: 'Enter a tag name',
    articleCount: 'Articles', createdAt: 'Created', updatedAt: 'Updated', actions: 'Actions',
    empty: 'No tags', deleting: 'Deleting…', deleteTitle: 'Delete tag',
    deleteConfirm: 'Delete the tag “{name}”?', nameRequired: 'Enter a tag name',
    nameTooLong: 'Tag names must be 50 characters or fewer', loadFailed: 'Failed to load tags',
    createSuccess: 'Tag created', updateSuccess: 'Tag updated', deleteSuccess: 'Tag deleted',
    createFailed: 'Failed to create tag', updateFailed: 'Failed to update tag', deleteFailed: 'Failed to delete tag',
    selectLabel: 'Tags', selectPlaceholder: 'Select tags', selectCategoryFirst: 'Select a category first', optionsFailed: 'Failed to load tag options',
    noOptions: 'Create tags in Tag management first', noCategoryOptions: 'No tags are linked to this category'
  },
  tagFilter: {
    label: 'Filter by tags', placeholder: 'Select tags', selected: '{count} tags selected',
    clear: 'Clear tags', empty: 'No articles match the selected filters'
  },
  auth: {
    login: 'Log in', register: 'Sign up', username: 'Username', email: 'Email',
    password: 'Password', confirmPassword: 'Confirm password',
    welcomeBack: 'Welcome back. Log in to your account',
    createAccount: 'Create an account and start documenting',
    forgotPassword: 'Forgot password?', noAccount: 'New here?', createOne: 'Create an account',
    hasAccount: 'Already have an account?', loginNow: 'Log in',
    usernamePlaceholder: 'Enter your username', emailPlaceholder: 'Enter your email',
    passwordPlaceholder: 'Enter your password', confirmPasswordPlaceholder: 'Enter your password again',
    lengthHint: '5–16 non-space characters', requiredUsername: 'Enter your username',
    requiredEmail: 'Enter your email', invalidEmail: 'Enter a valid email address',
    requiredPassword: 'Enter your password', requiredConfirmPassword: 'Enter your password again',
    invalidLength: 'Use 5–16 non-space characters', passwordMismatch: 'Passwords do not match',
    loginSuccess: 'Logged in successfully', loginFailed: 'Login failed',
    requestFailed: 'Request failed. Please try again', registering: 'Signing up…',
    formInvalid: 'Check and correct the registration details',
    registerFailed: 'Registration failed. Please try again',
    registerSuccess: 'Account created. Please log in'
  },
  profile: {
    loginAvatar: 'Login avatar', changeAvatar: 'Change avatar', avatarUploading: 'Uploading…',
    avatarTypeInvalid: 'Only JPG, PNG, and WebP images are supported',
    avatarSizeInvalid: 'The image must not exceed 2 MB',
    avatarResolutionLow: 'This image may look blurry. Use at least 200 × 200 when possible',
    avatarUploadSuccess: 'Avatar uploaded. Save your account profile to apply it',
    avatarUploadFailed: 'Image upload failed', avatarSaveSuccess: 'Avatar saved',
    avatarSaveFailed: 'Failed to save avatar', saveSuccess: 'Account profile saved',
    saveFailed: 'Failed to save account profile', loadFailed: 'Failed to load account profile'
  },
  comment: {
    title: 'Comments', refresh: 'Refresh', replyTo: 'Replying to @{name}', cancelReply: 'Cancel reply',
    inputStyle: 'Comment input style', fontSize: 'Size', font: 'Font',
    small: 'Small', medium: 'Medium', large: 'Large', sans: 'Sans serif', serif: 'Serif', mono: 'Monospace',
    deviceOnly: 'Input preferences are saved on this device only', loginToJoin: 'Log in to join the discussion',
    shortcut: 'Ctrl + Enter to publish · {count}/1000', loginToComment: 'Log in to comment',
    publishing: 'Publishing…', publishReply: 'Post reply', publish: 'Post comment',
    loading: 'Loading comments…', reload: 'Reload', emptyTitle: 'No comments yet',
    emptyHint: 'Start the conversation with the first comment.',
    placeholder: 'Share a thoughtful response or idea…', replyPlaceholder: 'Reply to @{name}…',
    loginRequired: 'Log in before replying', published: 'Comment published',
    publishFailed: 'Could not publish comment', loadFailed: 'Could not load comments. Please try again later',
    deleteTitle: 'Delete comment', deleteMessage: 'Deleted comments cannot be recovered. Continue?',
    confirmDelete: 'Delete', deleted: 'Comment deleted', deleteFailed: 'Could not delete comment',
    reply: 'Reply', delete: 'Delete', deactivatedUser: 'Deactivated user',
    avatarAlt: '{name} avatar'
  },
  message: {
    loadFailed: 'Could not load data', operationSuccess: 'Operation completed',
    operationFailed: 'Operation failed', loginRequired: 'Please log in', loggedOut: 'Logged out'
  },
  language: { label: 'Language', chinese: '中文', japanese: '日本語', english: 'English' },
  theme: { toLight: 'Switch to light mode', toDark: 'Switch to dark mode' },
  footer: { builtWith: 'Built with Spring Boot + Vue 3', location: 'Tokyo 🌸' },
  about: {
    aboutMe: 'About me', introPending: '(Introduction coming soon)',
    techStack: 'Tech stack', contactMe: 'Contact', role: 'Java Backend Developer · Tokyo'
  },
  siteBackground: {
    menu: 'Backgrounds', title: 'Site backgrounds', subtitle: 'Manage the background images used by the site-wide carousel.', filters: 'Background filters',
    create: 'Add background', edit: 'Edit', actions: 'Actions', createTitle: 'Add background', editTitle: 'Edit background',
    image: 'Background image', backendTitle: 'Internal name', enabledLabel: 'Enabled', sortOrder: 'Sort weight',
    sortHint: 'Higher values appear first', uploadImage: 'Upload image', replaceImage: 'Replace image', preview: 'Image preview',
    previewFailed: 'Preview unavailable', enabled: 'Enabled', disabled: 'Disabled', allStatus: 'All statuses',
    searchPlaceholder: 'Search by name', imageUrlPlaceholder: 'Upload an image or enter its URL', titlePlaceholder: 'Optional; used only in the admin area',
    createdAt: 'Created', updatedAt: 'Updated', saveSuccess: 'Saved successfully', saveFailed: 'Could not save',
    deleteSuccess: 'Deleted successfully', deleteFailed: 'Could not delete', deleteTitle: 'Delete background',
    deleteConfirm: 'Delete the background “{title}”?', deleteOssNotice: 'Deleting this record will not delete the uploaded file.',
    empty: 'No background images', loadFailed: 'Could not load backgrounds', detailFailed: 'Could not load background details',
    updateStatusFailed: 'Could not update the status; the previous value was restored', uploadSuccess: 'Image uploaded', uploadFailed: 'Upload failed',
    imageInvalid: 'Choose an image no larger than 5 MB',
    validation: { imageRequired: 'Upload a background image or enter its URL', imageLength: 'The image URL must be 500 characters or fewer', imageUrl: 'Enter a valid HTTP(S) image URL', titleLength: 'The internal name must be 100 characters or fewer', enabled: 'Enabled must be 0 or 1', sortOrder: 'Sort weight must be an integer', sortRange: 'Sort weight must be between -100000 and 100000' }
  },
  media: {
    title: 'Media Log', detailTitle: 'Media details', subtitle: 'A quiet record of stories read, watched, and played.', filters: 'Media filters', all: 'All',
    timeline: 'Timeline', grid: 'Grid', viewMode: 'View mode', sort: 'Sort', sortLatest: 'Latest finished', sortRating: 'Highest rated',
    unrated: 'Not rated', scoreOutOfTen: '{score}/10', noFinishedDate: 'Not finished', pendingSection: 'In progress / Planned',
    noShortReview: 'No short review yet', empty: 'No media records', emptyHint: 'Try another filter or check back later.',
    loadFailed: 'Could not load the media log', notFound: 'This media record was not found', backToLog: 'Back to Media Log',
    cover: 'Cover', workTitle: 'Title', typeLabel: 'Type', statusLabel: 'Status', rating: 'Rating', shortReview: 'Short review',
    longReview: 'Full review', finishedDate: 'Finished date',
    type: { book: 'Books', movie: 'Movies', anime: 'Anime', game: 'Games', unknown: 'Unknown' },
    section: { label: 'Media categories', game: 'Games', anime: 'Anime', movie: 'Movies', book: 'Books', archive: 'Interest archive', empty: 'No works in this section yet' },
    status: {
      unknown: 'Unknown', generic: { planned: 'Planned', inProgress: 'In progress', completed: 'Completed', dropped: 'Dropped' },
      book: { planned: 'Want to read', inProgress: 'Reading', completed: 'Read', dropped: 'Dropped' },
      movie: { planned: 'Want to watch', inProgress: 'Watching', completed: 'Watched', dropped: 'Dropped' },
      anime: { planned: 'Want to watch', inProgress: 'Watching', completed: 'Watched', dropped: 'Dropped' },
      game: { planned: 'Want to play', inProgress: 'Playing', completed: 'Completed', dropped: 'Dropped' }
    },
    admin: {
      menu: 'Media Log', create: 'New record', edit: 'Edit', actions: 'Actions', createTitle: 'New media record', editTitle: 'Edit media record',
      searchPlaceholder: 'Search by title', datePlaceholder: 'Select a finished date', coverPlaceholder: 'Enter an http / https cover URL', uploadCover: 'Upload cover',
      ratingHint: 'Half a star = 1 point; five stars = 10 points', ratingDisplay: '{stars} stars / {score} points', updatedAt: 'Updated', titleRequired: 'Enter a title', titleTooLong: 'Titles must be 200 characters or fewer',
      typeRequired: 'Select a type', statusRequired: 'Select a status', coverTooLong: 'Cover URLs must be 500 characters or fewer',
      coverInvalid: 'Only http / https cover URLs are allowed', shortReviewTooLong: 'Short reviews must be 500 characters or fewer', ratingInvalid: 'Rating must be an integer from 1 to 10 or blank',
      loadFailed: 'Could not load media records', detailFailed: 'Could not load media details', saveSuccess: 'Saved', saveFailed: 'Could not save',
      deleteTitle: 'Delete media record', deleteConfirm: 'Delete “{title}”?', deleteSuccess: 'Deleted', deleteFailed: 'Could not delete',
      imageInvalid: 'Use an image no larger than 5MB', uploadSuccess: 'Cover uploaded', uploadFailed: 'Could not upload cover'
    }
  },
  page: {
    home: 'Home', login: 'Log in', register: 'Sign up', about: 'About',
    archive: 'Archive', articleDetail: 'Article', profile: 'Profile',
    changePassword: 'Change password', admin: 'Admin', adminMedia: 'Media Log', adminBackground: 'Backgrounds', notes: 'Notes'
  }
}
