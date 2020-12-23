# USE-CASE


# Mô hình


# FM Gateway
1. Cơ chế tự bảo vệ:
- Hỗ trợ cấu hình giới hạn TPS
- Hỗ trợ cấu hình cảnh báo vượt ngưỡng bằng cảnh báo (bao gồm thông tin ngưỡng, mức severity của cảnh báo,...)
- enable/disable khi NE có dấu hiệu "DDos"(tránh a/h đến tải hệ thống), chú ý mô hình vật lý vs ảo hóa (vd nếu triển khai mano => cần api gọi tương tác ....)

# FM Core

2. Phát hiện NE bất thường
- có recommend/warning khi NE ko gửi thông tin "định kỳ"

# Tính năng đồng bộ alarmCategory


# Caching cảnh báo hiện tại 


- Đảm bảo ko mất alarmCategory trong hệ thống : cái này có thẻ theo nhiều hướng, vd sync, lưu trong db/queue ...
- mô hình: NE <=> [ FM Gateway <=>  ,.... <=> store DB ] => @ĐứcLM22 đ/g lại giải pháp, vd dùng queue/inmem db....

