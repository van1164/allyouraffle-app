import shared


class MyPageObserver: ObservableObject,BaseObserver {

    private var viewModel = MyPageViewModel()
    @Published var loading : Bool = false
    @Published var error : String? = nil
    @Published var userInfo : UserInfoResponse? = nil
    
    init() {
        viewModel.loading.subscribe{[weak self] load in
            self?.loading = load as! Bool
        }
        
        viewModel.error.subscribe{[weak self] err in
            self?.error = err as! String?
        }
        viewModel.userInfo.subscribe{[weak self] userInfo in
            self?.userInfo = userInfo as! UserInfoResponse?
        }
    }
    
    func initUserInfo(){
        Task{
            do{
                if let jwt = loadJwt(){
                    try await viewModel.doInitUserInfo(jwt:jwt)
                }
            }catch{
                
            }
        }
    }
    
    func getUserInfo(){
        Task{
            do{
                if let jwt = loadJwt(){
                    try await viewModel.getUserInfo(jwt:jwt)
                }
            }catch{
                
            }
        }
    }
    
    func setError(message: String) {
        viewModel.setError(message: message)
    }
    
    func setErrorNull() {
        viewModel.setNullError()
    }
}
