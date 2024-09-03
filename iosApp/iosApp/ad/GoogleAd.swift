import GoogleMobileAds

@MainActor
class RewardedViewModel: NSObject, ObservableObject, GADFullScreenContentDelegate {
    @Published var coins = 0
    @Published var adLoading : Bool = false
    @Published var error : String? = nil
    @Published var rewardedAd: GADRewardedAd? = nil
    
    
    func loadAd(fail : @escaping () -> Void,success : @escaping () -> Void) {
        Task {
            do {
                print("AAAAAAAAAAAAAA")
                rewardedAd = try await GADRewardedAd.load(
                    withAdUnitID: "ca-app-pub-7372592599478425/7706687595", request: GADRequest())
                print("BBBBBBBBBBBBBB")
                rewardedAd?.fullScreenContentDelegate = self
                print("XXXXXXXXXXXXXXXXXXXXXXXXXXX")
                print(rewardedAd)
                if(rewardedAd == nil){
                    fail()
                }
                else{
                    success()
                }
//                showAd(onload:onload)
            } catch {
                print("Failed to load rewarded ad with error: \(error.localizedDescription)")
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
