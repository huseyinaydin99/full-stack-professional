import SidebarWithHeader from "./components/shared/SideBar.jsx";
import {Text} from "@chakra-ui/react";

const Home = () => {

    return (
        <SidebarWithHeader>
            <Text fontSize={"6xl"}>Yönetim Paneli</Text>
        </SidebarWithHeader>
    )
}

export default Home;