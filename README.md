# item-frame-video
Bukkit util for playing videos on item frames, uses ffmpeg for decoding videos. I found that playing videos on item frames just isn't doable for large networks, this should be used for personal use only due to network constraints.

### Example
```java
ItemFramePlayer player = new ItemFramePlayer(plugin, itemFrames, new File("video.mp4"));
player.addViewer(Bukkit.getPlayer("Respect"));
player.play();
```
