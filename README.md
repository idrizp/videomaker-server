# videomaker

A fully-functioning video-generator.

- Tech used:
- - OpenAI - Used in order to generate keywords to use for Pexels' API to obtain stock video clips.
- - FFMPEG - Used to stitch together the stock video clips obtained from Pexels.
- - Replicate - Used for the text-to-speech functionality that will be used in the video.

TODO:
- [ ] Create a client for the videomaker.
- [x] Authentication
- [x] Keyword generation
- [x] Stock video retrieval using Pexels' API
- [x] Database models for the video data
- [x] Webhook for TTS functionality from Replicate.

Endpoints:
- /api/auth/login - POST - Login endpoint.
- /api/auth/register - POST - Register endpoint.
- /api/video/order - POST - Orders a new video.
- /api/video/orders - GET - Retrieves all orders and their statuses.
- /api/profile/me - GET - Returns the authenticated user's profile details.
- /webhook/tts - POST - Webhook for TTS functionality provided by Replicate.