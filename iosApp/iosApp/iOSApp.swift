import SwiftUI
import FirebaseCore
import GoogleSignIn
import GoogleMobileAds
import AppTrackingTransparency
import AdSupport

func requestTrackingAuthorization() {
    if #available(iOS 14, *) {
        ATTrackingManager.requestTrackingAuthorization { status in
            switch status {
            case .authorized:
                // 추적 권한을 허용한 경우
                print("Tracking authorized")
            case .denied:
                // 추적 권한을 거부한 경우
                print("Tracking denied")
            case .restricted:
                // 제한된 경우 (사용자가 추적을 제한한 경우)
                print("Tracking restricted")
            case .notDetermined:
                // 사용자가 아직 결정하지 않은 경우
                print("Tracking not determined")
            @unknown default:
                // 기타 예상하지 못한 상태
                print("Unknown tracking status")
            }
        }
    } else {
        // iOS 14 이하에서는 따로 권한을 요청할 필요 없음
        print("Tracking is not required for iOS versions below 14")
    }
}
class AppDelegate: NSObject, UIApplicationDelegate {
  func application(_ application: UIApplication,
                   didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
    FirebaseApp.configure()
    requestTrackingAuthorization()
    GADMobileAds.sharedInstance().requestConfiguration.testDeviceIdentifiers = [ "ee6ad9a2a74cf421a8248de754a9c2ed" ]
    GADMobileAds.sharedInstance().start(completionHandler: nil)

    return true
  }
    
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        return GIDSignIn.sharedInstance.handle(url)
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
