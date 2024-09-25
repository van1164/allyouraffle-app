import GoogleMobileAds
import SwiftUI

@MainActor
class RewardedViewModel: NSObject, ObservableObject, GADFullScreenContentDelegate {
    @Published var coins = 0
    @Published var adLoading : Bool = false
    @Published var error : String? = nil
    @Published var rewardedAd: GADRewardedAd? = nil
    
    
    func loadAd(fail : @escaping () -> Void,success : @escaping () -> Void) {
        Task {
            do {
                let request = GADRequest()
                let extras = GADExtras()
                extras.additionalParameters = ["suppress_test_label": "1"]
                request.register(extras)
                rewardedAd = try await GADRewardedAd.load(
                    withAdUnitID: "ca-app-pub-7372592599478425/7706687595", request: request)
                rewardedAd?.fullScreenContentDelegate = self
                if(rewardedAd == nil){
                    fail()
                }
                else{
                    success()
                }
            } catch {
                fail()
            }
        }
    }
    
    nonisolated func adDidRecordImpression(_ ad: GADFullScreenPresentingAd) {
      print("\(#function) called")
    }

    nonisolated func adDidRecordClick(_ ad: GADFullScreenPresentingAd) {
      print("\(#function) called")
    }

    nonisolated func ad(
      _ ad: GADFullScreenPresentingAd,
      didFailToPresentFullScreenContentWithError error: Error
    ) {
      print("\(#function) called")
    }

    nonisolated func adWillPresentFullScreenContent(_ ad: GADFullScreenPresentingAd) {
      print("\(#function) called")
    }

    nonisolated func adWillDismissFullScreenContent(_ ad: GADFullScreenPresentingAd) {
      print("\(#function) called")
    }

    func adDidDismissFullScreenContent(_ ad: GADFullScreenPresentingAd) {
      print("\(#function) called")
      // Clear the rewarded ad.
      rewardedAd = nil
    }
    
    func showAd(onSuccess : @escaping () -> Void) {
      guard let rewardedAd = rewardedAd else {
        error = "광고를 불러오는 데 실패했습니다."
        return print("Ad wasn't ready.")
      }
        print("CCCCCCCCCCCCCCCCCCCCc")

      rewardedAd.present(fromRootViewController: nil) {
        let reward = rewardedAd.adReward
        onSuccess()
        print("Reward amount: \(reward.amount)")
      }
    }
}



struct AdBannerView: UIViewControllerRepresentable {
    let adUnitID: String
    let bannerView = GADBannerView(adSize: GADAdSizeBanner)
    func makeUIViewController(context: Context) -> UIViewController {
        let viewController = UIViewController()
        
        bannerView.adUnitID = adUnitID
    
        bannerView.rootViewController = viewController
        viewController.view.addSubview(bannerView)
        
        
        return viewController

    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        print("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ")
        let request = GADRequest()
        let extras = GADExtras()
        extras.additionalParameters = ["suppress_test_label": "1"]
        request.register(extras)
        bannerView.load(request)
    }
}
