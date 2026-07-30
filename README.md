*Sơ đồ kiến trúc
```text
┌─────────────────────────────────────────────────────────┐
│                    API Gateway                          │
│              (Routes & Global Logging)                  │
└──────────────┬──────────────────────────────────────────┘
               │
        ┌──────┴────────┐
        │               │
    ┌────▼─────┐    ┌───▼──────┐
    │ Order    │    │ Inventory│
    │ Service  │    │ Service  │
    └────┬─────┘    └────┬─────┘
         │               │
         └───────┬───────┘
                 │
           ┌─────▼──────┐
           │   Eureka   │
           │  (Registry)│
           └────────────┘

Communication Methods:
• Sync: OpenFeign
```
Câu 1 Lý do Gateway không gọi trực tiếp đê IP/Port
- Là để tránh hardcode nếu gateway gọi trực tiếp đến IP/Port của Service, khi service thay đổi IP/Port thì gateway
sẽ không thể kết nối được
- Thay vào đó gateway sẽ gọi đến tên miền hoặc hostname của service và DNS sẽ giúp định tuyến đến IP/Port hiện tại
của service

Câu 2 Scale OrderService khi số lượng request tăng đột biến mà không thay đổi cấu hình gateway
-Khi số lượng request tăng đột biến, chúng ta có thể scale OrderService bằng cách tăng số lượng instance của
service maf không cần thay đổi cấu hình gateway

Câu 3 So sánh ưu/nhược điểm của OpenFeign va Kafka:
-OpenFeign
  -Ưu:
    -Dễ dàng tích hợp SpringBoot
    -Hỗ trợ LoadBalancing và retry tự động
    -Cung cấp cách tiếp cận đồng bộ, dễ hiểu cho các request HTTP
  -Nhược:
    -Không phù hợp cho các hệ thống cần xử lý bất đồng bộ hoặc có độ trễ cao
    -Không tối ưu cho các trường hợp cần xử lý dữ liệu lớn hoặc streaming
-Kafka
  -Ưu:
    -Hỗ trị xử lý bất đồng bộ và có khả năng chịu tải cao
    -Thích hợp cho trường hợp cần xử lý dữ liệu lớn hoặc streaming
    -Cung cấp cơ chế lưu trữ và phân phối tin nhắn, giúp đảm bảo tính bền vững và khả năng mở rộng
  -Nhuợc
     -Cần phải thiết lập quản lý một hệ thống kafka, điều này phức tạp hợp so với việc sử dụng OpenFeign
     -Đòi hỏi kiến thức về Kafka, cách triển khai các Producer/Consumer, điều này làm tăng độ phức tạp cho các
    nhà phát triển không quen với hệ thống này
